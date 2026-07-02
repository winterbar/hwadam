document.addEventListener("DOMContentLoaded", function () {

  // 로그인 사용자 아이디 가져오기
  function getLoginUsername() {
    return document.body.dataset.loginUsername
      ? document.body.dataset.loginUsername.trim()
      : "";
  }


  // CSRF 토큰 가져오기
  function getCsrfHeaders() {
    const csrfToken = document.querySelector('meta[name="_csrf"]');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]');

    const headers = {};

    if (csrfToken && csrfHeader) {
      headers[csrfHeader.getAttribute("content")] =
        csrfToken.getAttribute("content");
    }

    return headers;
  }


  // 댓글 데이터 이름 통일
  function getReplyId(reply) {
    return reply.replyId || reply.reply_id || "";
  }

  function getParentsReplyId(reply) {
    return reply.parentsReplyId || reply.parents_reply_id || "";
  }

  function getReplyContent(reply) {
    return reply.replyContent || reply.reply_content || "";
  }

  function getParentNickname(reply) {
    return reply.parentNickname || reply.parent_nickname || "";
  }

  function getNickname(reply) {
    return reply.nickname && reply.nickname.trim() !== ""
      ? reply.nickname
      : reply.username;
  }

  function getProfileImage(reply) {
    return (
      reply.profileImage ||
      reply.profile_image ||
      reply.savedName ||
      reply.saved_name ||
      ""
    );
  }


  // 답글 수정 시 @닉네임 멘션 제외하고 내용만 가져오기
  function getOnlyReplyText(contentEl) {
    if (!contentEl) return "";

    const clone = contentEl.cloneNode(true);
    const mention = clone.querySelector(".comment-mention");

    if (mention) {
      mention.remove();
    }

    return clone.textContent.trim();
  }


  // 댓글 / 답글 기능
  function initCommentArea() {
    document.querySelectorAll(".feed-card").forEach(function (card) {
      const commentToggleBtn = card.querySelector(".feed-comment-toggle-btn");
      const commentBox = card.querySelector(".feed-comment-box");
      const commentListBtn = card.querySelector(".feed-comment-count");
      const actionCommentCount = card.querySelector(
        ".feed-comment-action-count",
      );
      const commentList = card.querySelector(".feed-comment-list");
      const commentForm = card.querySelector(".feed-comment-form");

      const commentInput = commentForm
        ? commentForm.querySelector('input[name="replyContent"]')
        : null;

      const parentInput = commentForm
        ? commentForm.querySelector(".reply-parent-id-input")
        : null;

      const replyTarget = card.querySelector(".comment-reply-target");
      const replyTargetWriter = card.querySelector(
        ".comment-reply-target-writer",
      );
      const replyTargetContent = card.querySelector(
        ".comment-reply-target-content",
      );
      const replyCancelBtn = card.querySelector(".comment-reply-cancel-btn");

      let commentCount = Number(
        commentListBtn ? commentListBtn.dataset.commentCount || 0 : 0,
      );

      if (Number.isNaN(commentCount)) {
        commentCount = 0;
      }

      normalizeServerRenderedComments();
      updateCommentCountText(false);
      applyCommentOwnerMenus();
      setupReplyMoreButtons();

      if (commentBox) {
        commentBox.style.display = "none";
      }

      if (commentList) {
        commentList.style.display = "none";
      }

      // 댓글 입력창 열기 / 닫기
      if (commentToggleBtn && commentBox) {
        commentToggleBtn.addEventListener("click", function () {
          const isOpen = commentBox.style.display === "block";
          commentBox.style.display = isOpen ? "none" : "block";

          if (!isOpen && commentInput) {
            commentInput.focus();
          }
        });
      }

      // 댓글 목록 열기 / 닫기
      if (commentListBtn && commentList) {
        commentListBtn.addEventListener("click", function () {
          const isOpen = commentList.style.display === "block";

          commentList.style.display = isOpen ? "none" : "block";
          updateCommentCountText(!isOpen);
        });
      }

      // 답글 대상 취소
      if (replyCancelBtn) {
        replyCancelBtn.addEventListener("click", function () {
          clearReplyTarget();
        });
      }

      // 댓글 등록 / 답글 등록
      if (commentForm && commentInput) {
        commentForm.addEventListener("submit", function (event) {
          event.preventDefault();

          const action = commentForm.getAttribute("action");
          const formData = new FormData(commentForm);
          const submitContent = commentInput.value.trim();

          if (!submitContent) return;

          formData.set("replyContent", submitContent);

          fetch(action, {
            method: "POST",
            headers: getCsrfHeaders(),
            body: formData,
          })
            .then(function (response) {
              return response.json();
            })
            .then(function (replyList) {
              renderReplyList(replyList);

              commentInput.value = "";
              clearReplyTarget();

              commentCount = Array.isArray(replyList) ? replyList.length : 0;

              if (commentListBtn) {
                commentListBtn.dataset.commentCount = commentCount;
              }

              if (commentToggleBtn) {
                commentToggleBtn.dataset.commentCount = commentCount;
              }

              if (actionCommentCount) {
                actionCommentCount.textContent = commentCount;
              }

              if (commentList) {
                commentList.style.display = "block";
              }

              updateCommentCountText(true);
            })
            .catch(function (error) {
              console.log(error);
            });
        });
      }

      // 댓글 목록 클릭 이벤트
      if (commentList) {
        commentList.addEventListener("click", function (event) {
          const mentionEl = event.target.closest(".comment-mention");
          const commentBubble = event.target.closest(
            ".feed-comment-bubble, .comment-child-bubble",
          );

          // @멘션 클릭 시 해당 댓글로 이동
          if (mentionEl) {
            event.stopPropagation();
            moveToMentionTarget(mentionEl);
            return;
          }

          // 댓글 말풍선 클릭 시 답글 대상 설정
          if (commentBubble) {
            event.stopPropagation();

            const commentItem = event.target.closest("[data-reply-id]");

            if (
              commentItem &&
              !commentItem.classList.contains("feed-comment-empty")
            ) {
              setReplyTarget(commentItem);
            }
          }
        });
      }


      // 서버에서 처음 렌더링된 댓글 내용 정리
      function normalizeServerRenderedComments() {
        if (!commentList) return;

        commentList
          .querySelectorAll(".feed-comment-item")
          .forEach(function (item) {
            if (item.classList.contains("feed-comment-empty")) return;

            const contentEl = item.querySelector(".feed-comment-content");
            const replyContent = item.dataset.replyContent || "";

            if (contentEl) {
              renderReplyContent(contentEl, replyContent, "", "");
            }
          });

        commentList
          .querySelectorAll(".comment-child-item")
          .forEach(function (item) {
            const contentEl = item.querySelector(".comment-child-content");
            const replyContent = item.dataset.replyContent || "";
            const parentNickname = item.dataset.parentNickname || "";
            const parentsReplyId = item.dataset.parentsReplyId || "";

            if (contentEl) {
              renderReplyContent(
                contentEl,
                replyContent,
                parentNickname,
                parentsReplyId,
              );
            }
          });
      }


      // 로그인한 사용자가 쓴 댓글에만 ... 버튼 보이게
      function applyCommentOwnerMenus() {
        const loginUsername = getLoginUsername();

        if (!commentList) return;

        commentList
          .querySelectorAll(".feed-comment-item, .comment-child-item")
          .forEach(function (item) {
            if (item.classList.contains("feed-comment-empty")) return;

            const writerUsername = item.dataset.username
              ? item.dataset.username.trim()
              : "";

            const bubble = item.querySelector(
              ".feed-comment-bubble, .comment-child-bubble",
            );

            const moreWrap = bubble
              ? bubble.querySelector(".comment-more-wrap")
              : null;

            if (!moreWrap) return;

            if (
              loginUsername &&
              loginUsername !== "anonymousUser" &&
              writerUsername &&
              loginUsername === writerUsername
            ) {
              moreWrap.classList.add("is-owner");
            } else {
              moreWrap.classList.remove("is-owner");
            }
          });
      }


      // 답글 대상 설정
      function setReplyTarget(commentItem) {
        if (!replyTarget || !parentInput || !commentInput) return;

        const replyId = commentItem.dataset.replyId || "";

        const writerEl = commentItem.querySelector(
          ".feed-comment-writer, .comment-child-writer",
        );

        const contentEl = commentItem.querySelector(
          ".feed-comment-content, .comment-child-content",
        );

        const writer =
          commentItem.dataset.nickname ||
          (writerEl ? writerEl.textContent.trim() : "회원");

        const content = getPureContentText(contentEl);

        parentInput.value = replyId;

        if (replyTargetWriter) {
          replyTargetWriter.textContent = writer;
        }

        if (replyTargetContent) {
          replyTargetContent.textContent = content;
        }

        replyTarget.classList.add("is-active");

        if (commentBox) {
          commentBox.style.display = "block";
        }

        commentInput.placeholder = writer + "님에게 답글 달기...";
        commentInput.focus();
      }


      // 답글 대상 초기화
      function clearReplyTarget() {
        if (parentInput) {
          parentInput.value = "";
        }

        if (replyTarget) {
          replyTarget.classList.remove("is-active");
        }

        if (replyTargetWriter) {
          replyTargetWriter.textContent = "";
        }

        if (replyTargetContent) {
          replyTargetContent.textContent = "";
        }

        if (commentInput) {
          commentInput.placeholder = "댓글 달기...";
        }
      }

      // 답글 접기 / 펼치기
      function setupReplyMoreButtons() {
        if (!commentList) return;

        commentList
          .querySelectorAll(".feed-comment-item")
          .forEach(function (commentItem) {
            const childList = Array.from(commentItem.children).find(
              function (child) {
                return (
                  child.classList &&
                  child.classList.contains("comment-child-list")
                );
              },
            );

            if (!childList) return;

            const childItems = Array.from(childList.children).filter(
              function (child) {
                return (
                  child.classList &&
                  child.classList.contains("comment-child-item")
                );
              },
            );

            const childCount = childItems.length;

            const oldBtn = Array.from(commentItem.children).find(
              function (child) {
                return (
                  child.classList &&
                  child.classList.contains("comment-child-toggle-btn")
                );
              },
            );

            if (oldBtn) {
              oldBtn.remove();
            }

            if (childCount === 0) {
              childList.style.setProperty("display", "none", "important");
              return;
            }

            childList.style.setProperty("display", "none", "important");

            const toggleBtn = document.createElement("button");
            toggleBtn.type = "button";
            toggleBtn.className = "comment-child-toggle-btn";
            toggleBtn.textContent = "답글 " + childCount + "개 더 보기";

            commentItem.insertBefore(toggleBtn, childList);

            toggleBtn.addEventListener("click", function (event) {
              event.stopPropagation();

              const isClosed = childList.style.display === "none";

              if (isClosed) {
                childList.style.setProperty("display", "block", "important");
                toggleBtn.classList.add("is-open");
                toggleBtn.textContent = "답글 숨기기";
              } else {
                childList.style.setProperty("display", "none", "important");
                toggleBtn.classList.remove("is-open");
                toggleBtn.textContent = "답글 " + childCount + "개 보기";
              }
            });
          });
      }

      // 댓글 리스트 다시 그리기
      function renderReplyList(replyList) {
        if (!commentList) return;

        commentList.innerHTML = "";

        if (!Array.isArray(replyList) || replyList.length === 0) {
          const emptyItem = document.createElement("div");
          emptyItem.className = "feed-comment-item feed-comment-empty";

          const emptyText = document.createElement("span");
          emptyText.textContent = "아직 등록된 댓글이 없습니다.";

          emptyItem.appendChild(emptyText);
          commentList.appendChild(emptyItem);

          return;
        }

        const replyMap = {};
        const rootReplies = [];

        replyList.forEach(function (reply) {
          reply.children = [];
          reply._replyId = String(getReplyId(reply));
          reply._parentsReplyId = getParentsReplyId(reply)
            ? String(getParentsReplyId(reply))
            : "";

          if (reply._replyId) {
            replyMap[reply._replyId] = reply;
          }
        });

        function findRootReply(reply) {
          let current = reply;
          let safetyCount = 0;

          while (
            current._parentsReplyId &&
            replyMap[current._parentsReplyId] &&
            safetyCount < 30
          ) {
            current = replyMap[current._parentsReplyId];
            safetyCount++;
          }

          return current;
        }

        replyList.forEach(function (reply) {
          if (!reply._parentsReplyId) {
            rootReplies.push(reply);
            return;
          }

          const parentReply = replyMap[reply._parentsReplyId];

          if (!getParentNickname(reply) && parentReply) {
            reply._resolvedParentNickname = getNickname(parentReply);
          } else {
            reply._resolvedParentNickname = getParentNickname(reply);
          }

          const rootReply = findRootReply(reply);

          if (rootReply && rootReply._replyId !== reply._replyId) {
            rootReply.children.push(reply);
          } else {
            rootReplies.push(reply);
          }
        });

        rootReplies.forEach(function (reply) {
          const commentItem = createCommentItem(reply);
          commentList.appendChild(commentItem);
        });

        applyCommentOwnerMenus();
        setupReplyMoreButtons();
      }

      // 부모 댓글 HTML 생성
      function createCommentItem(reply) {
        const commentItem = document.createElement("div");
        commentItem.className = "feed-comment-item";

        const replyId = getReplyId(reply);
        const username = reply.username || "";
        const writerName = getNickname(reply);

        if (replyId) {
          commentItem.dataset.replyId = replyId;
        }

        if (username) {
          commentItem.dataset.username = username;
        }

        if (writerName) {
          commentItem.dataset.nickname = writerName;
        }

        const bubble = document.createElement("div");
        bubble.className = "feed-comment-bubble";

        const avatar = document.createElement("div");
        avatar.className = "feed-comment-avatar";

        const profileImage = getProfileImage(reply);

        if (profileImage) {
          const img = document.createElement("img");
          img.src = "/upload/profile/" + profileImage;
          img.alt = "프로필 이미지";

          img.onerror = function () {
            img.remove();
            avatar.textContent = writerName ? writerName.substring(0, 1) : "회";
          };

          avatar.appendChild(img);
        } else {
          avatar.textContent = writerName ? writerName.substring(0, 1) : "회";
        }

        const main = document.createElement("div");
        main.className = "feed-comment-main";

        const writer = document.createElement("strong");
        writer.className = "feed-comment-writer";
        writer.textContent = writerName || "회원";

        const content = document.createElement("span");
        content.className = "feed-comment-content";

        renderReplyContent(content, getReplyContent(reply), "", "");

        main.appendChild(writer);
        main.appendChild(content);

        bubble.appendChild(avatar);
        bubble.appendChild(main);
        bubble.appendChild(createCommentMoreWrap());

        const childList = document.createElement("div");
        childList.className = "comment-child-list";

        if (Array.isArray(reply.children) && reply.children.length > 0) {
          reply.children.forEach(function (childReply) {
            childList.appendChild(createChildReplyItem(childReply));
          });
        }

        commentItem.appendChild(bubble);
        commentItem.appendChild(childList);

        return commentItem;
      }


      // 자식 댓글 HTML 생성
      function createChildReplyItem(reply) {
        const childItem = document.createElement("div");
        childItem.className = "comment-child-item";

        const replyId = getReplyId(reply);
        const username = reply.username || "";
        const parentsReplyId = getParentsReplyId(reply);
        const writerName = getNickname(reply);
        const parentNickname =
          reply._resolvedParentNickname || getParentNickname(reply);

        if (replyId) {
          childItem.dataset.replyId = replyId;
        }

        if (username) {
          childItem.dataset.username = username;
        }

        if (parentsReplyId) {
          childItem.dataset.parentsReplyId = parentsReplyId;
        }

        if (writerName) {
          childItem.dataset.nickname = writerName;
        }

        const bubble = document.createElement("div");
        bubble.className = "comment-child-bubble";

        const avatar = document.createElement("div");
        avatar.className = "comment-child-avatar";

        const profileImage = getProfileImage(reply);

        if (profileImage) {
          const img = document.createElement("img");
          img.src = "/upload/profile/" + profileImage;
          img.alt = "프로필 이미지";

          img.onerror = function () {
            img.remove();
            avatar.textContent = writerName ? writerName.substring(0, 1) : "회";
          };

          avatar.appendChild(img);
        } else {
          avatar.textContent = writerName ? writerName.substring(0, 1) : "회";
        }

        const main = document.createElement("div");
        main.className = "comment-child-main";

        const writer = document.createElement("strong");
        writer.className = "comment-child-writer";
        writer.textContent = writerName || "회원";

        const content = document.createElement("span");
        content.className = "comment-child-content";

        renderReplyContent(
          content,
          getReplyContent(reply),
          parentNickname,
          parentsReplyId,
        );

        main.appendChild(writer);
        main.appendChild(content);

        bubble.appendChild(avatar);
        bubble.appendChild(main);
        bubble.appendChild(createCommentMoreWrap());

        childItem.appendChild(bubble);

        return childItem;
      }

      // 댓글 내용 출력
      // 답글이면 @닉네임 멘션 붙이기
      function renderReplyContent(
        contentEl,
        text,
        parentNickname,
        parentReplyId,
      ) {
        const value = text || "";

        contentEl.innerHTML = "";

        if (parentNickname && parentReplyId) {
          const mention = document.createElement("span");
          mention.className = "comment-mention";
          mention.textContent = "@" + parentNickname;
          mention.dataset.targetReplyId = parentReplyId;

          contentEl.appendChild(mention);
          contentEl.appendChild(document.createTextNode(" " + value));
          return;
        }

        contentEl.textContent = value;
      }


      // 멘션 제외하고 댓글 내용 가져오기
      function getPureContentText(contentEl) {
        if (!contentEl) return "";

        const clone = contentEl.cloneNode(true);
        const mention = clone.querySelector(".comment-mention");

        if (mention) {
          mention.remove();
        }

        return clone.textContent.trim();
      }
      
      // 댓글/답글 ... 버튼 생성
      function createCommentMoreWrap() {
        const moreWrap = document.createElement("div");
        moreWrap.className = "comment-more-wrap";

        const moreBtn = document.createElement("button");
        moreBtn.type = "button";
        moreBtn.className = "comment-more-btn";
        moreBtn.textContent = "···";

        moreWrap.appendChild(moreBtn);

        return moreWrap;
      }

      // 댓글 모두 보기 / 접기 문구 변경
      function updateCommentCountText(isOpen) {
        if (!commentListBtn) return;

        if (isOpen) {
          commentListBtn.textContent = "댓글 접기";
        } else {
          commentListBtn.textContent = "댓글 모두 보기";
        }
      }
    });
  }

  // 댓글/답글 수정,삭제
  function initFloatingCommentMenu() {
    const floatingMenu = document.createElement("div");
    floatingMenu.className = "floating-comment-menu";

    const editButton = document.createElement("button");
    editButton.type = "button";
    editButton.textContent = "수정하기";

    const deleteButton = document.createElement("button");
    deleteButton.type = "button";
    deleteButton.textContent = "삭제하기";
    deleteButton.className = "danger";

    floatingMenu.appendChild(editButton);
    floatingMenu.appendChild(deleteButton);
    document.body.appendChild(floatingMenu);

    let currentCommentItem = null;

    function closeFloatingMenu() {
      floatingMenu.classList.remove("is-open");
      currentCommentItem = null;
    }

    document.addEventListener(
      "click",
      function (event) {
        const moreBtn = event.target.closest(".comment-more-btn");

        if (moreBtn) {
          event.preventDefault();
          event.stopPropagation();
          event.stopImmediatePropagation();

          currentCommentItem = moreBtn.closest("[data-reply-id]");

          if (!currentCommentItem) return;

          const rect = moreBtn.getBoundingClientRect();

          floatingMenu.style.position = "fixed";
          floatingMenu.style.top = rect.bottom + 6 + "px";
          floatingMenu.style.left = rect.left - 70 + "px";
          floatingMenu.style.right = "auto";
          floatingMenu.style.zIndex = "2147483647";

          floatingMenu.classList.add("is-open");
          return;
        }

        if (event.target.closest(".floating-comment-menu")) {
          return;
        }

        closeFloatingMenu();
      },
      true,
    );

    // 수정하기
    editButton.addEventListener("click", function (event) {
      event.preventDefault();
      event.stopPropagation();

      if (!currentCommentItem) return;

      const replyId = currentCommentItem.dataset.replyId;

      const contentEl = currentCommentItem.querySelector(
        ".feed-comment-content, .comment-child-content",
      );

      if (!contentEl) return;

      const oldMention = contentEl.querySelector(".comment-mention");
      const mentionText = oldMention ? oldMention.textContent : "";
      const mentionTargetId = oldMention ? oldMention.dataset.targetReplyId : "";

      const oldText = getOnlyReplyText(contentEl);

      contentEl.innerHTML = "";

      const input = document.createElement("input");
      input.type = "text";
      input.className = "comment-edit-input";
      input.value = oldText;

      const saveBtn = document.createElement("button");
      saveBtn.type = "button";
      saveBtn.className = "comment-save-btn";
      saveBtn.textContent = "저장";
      input.addEventListener("click", function (event) {
  event.stopPropagation();
});

input.addEventListener("mousedown", function (event) {
  event.stopPropagation();
});

input.addEventListener("mouseover", function (event) {
  event.stopPropagation();
});

saveBtn.addEventListener("click", function (event) {
  event.stopPropagation();
});

saveBtn.addEventListener("mousedown", function (event) {
  event.stopPropagation();
});

      contentEl.appendChild(input);
      contentEl.appendChild(saveBtn);

      input.focus();

      saveBtn.addEventListener("click", function (e) {
        e.stopPropagation();

        const newText = input.value.trim();

        if (!newText) return;

        const formData = new FormData();
        formData.append("replyContent", newText);

        fetch("/feed/reply/" + replyId + "/edit", {
          method: "POST",
          headers: getCsrfHeaders(),
          body: formData,
        }).then(function () {
          contentEl.innerHTML = "";

          if (mentionText && mentionTargetId) {
            const mention = document.createElement("span");
            mention.className = "comment-mention";
            mention.textContent = mentionText;
            mention.dataset.targetReplyId = mentionTargetId;

            contentEl.appendChild(mention);
            contentEl.appendChild(document.createTextNode(" " + newText));
          } else {
            contentEl.textContent = newText;
          }
        });
      });

      closeFloatingMenu();
    });

    // 삭제하기
    deleteButton.addEventListener("click", function (event) {
      event.preventDefault();
      event.stopPropagation();

      if (!currentCommentItem) return;

      const replyId = currentCommentItem.dataset.replyId;

      fetch("/feed/reply/" + replyId + "/delete", {
        method: "POST",
        headers: getCsrfHeaders(),
      }).then(function () {
        const contentEl = currentCommentItem.querySelector(
          ".feed-comment-content, .comment-child-content",
        );

        if (contentEl) {
          contentEl.textContent = "사용자가 삭제한 댓글입니다.";
          contentEl.classList.add("deleted-comment-text");
        }

        const moreWrap = currentCommentItem.querySelector(".comment-more-wrap");

        if (moreWrap) {
          moreWrap.remove();
        }

        closeFloatingMenu();
      });
    });

    window.addEventListener("scroll", closeFloatingMenu);
    window.addEventListener("resize", closeFloatingMenu);
    
  }
  

  // 실행
  initCommentArea();
  initFloatingCommentMenu();
});