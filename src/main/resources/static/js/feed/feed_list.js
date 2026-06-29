document.addEventListener("DOMContentLoaded", function () {

  const filterBar = document.getElementById("feed-filter-bar");
  const filterSummary = document.getElementById("feed-filter-summary");
  const filterToggleIcon = document.getElementById("feed-filter-toggle-icon");
  const filterInner = document.getElementById("feed-filter-inner");
  const feedEmpty = document.getElementById("feed-empty");


  // 현재 로그인한 사용자 아이디 가져오기
  function getLoginUsername() {
    return document.body.dataset.loginUsername
      ? document.body.dataset.loginUsername.trim()
      : "";
  }

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


  // 댓글 데이터 이름 통일 함수
  
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


  //  해시태그 중복 제거 기능
  function removeDuplicateTagButtons() {
    const tagButtons = document.querySelectorAll(".feed-tag-btn");
    const seenTags = new Set();

    tagButtons.forEach(function (button) {
      const tag = button.dataset.tag
        ? button.dataset.tag.replace("#", "").trim().toLowerCase()
        : "";

      if (!tag) return;

      if (seenTags.has(tag)) {
        button.remove();
      } else {
        seenTags.add(tag);
      }
    });
  }


  // 피드 내용
  function renderFeedContentAndLink() {
    const feedCards = document.querySelectorAll(".feed-card");

    feedCards.forEach(function (card) {
      const captionTextEl = card.querySelector(".js-caption-text");
      const link = card.querySelector(".feed-share-link");

      if (!captionTextEl) return;

      const rawContent = captionTextEl.textContent
        ? captionTextEl.textContent
        : card.dataset.content
          ? card.dataset.content
          : "";

      if (!rawContent.trim()) return;

      let contentText = rawContent;

      if (link) {
        link.style.display = "none";

        const urlRegex = /(https?:\/\/[^\s]+)/;
        const urlMatch = rawContent.match(urlRegex);

        if (urlMatch) {
          const linkText = urlMatch[0];

          contentText = rawContent.replace(linkText, "").trim();

          link.href = linkText;
          link.style.display = "inline-flex";
        }
      }

      captionTextEl.textContent = contentText.trim();
    });
  }

  // 피드 이미지 출력 기능

  function renderFeedImages() {
    const feedCards = document.querySelectorAll(".feed-card");

    feedCards.forEach(function (card) {
      const imageEl = card.querySelector(".js-feed-image");
      const placeholder = card.querySelector(".js-feed-placeholder");
      const badge = card.querySelector(".js-photo-badge");
      const prevBtn = card.querySelector(".feed-photo-prev");
      const nextBtn = card.querySelector(".feed-photo-next");

      const rawImage = card.dataset.images ? card.dataset.images.trim() : "";

      const images =
        rawImage && rawImage !== "null" && rawImage !== "undefined"
          ? [rawImage]
          : [];

      let currentIndex = 0;

      function updateImage() {
        if (images.length === 0) {
          if (imageEl) {
            imageEl.removeAttribute("src");
            imageEl.style.display = "none";
          }

          if (placeholder) {
            placeholder.style.display = "flex";
          }

          if (badge) {
            badge.style.display = "none";
          }

          if (prevBtn) {
            prevBtn.style.display = "none";
          }

          if (nextBtn) {
            nextBtn.style.display = "none";
          }

          return;
        }

        if (imageEl) {
          imageEl.src = "/upload/" + images[currentIndex];
          imageEl.style.display = "block";
        }

        if (placeholder) {
          placeholder.style.display = "none";
        }

        if (badge) {
          badge.style.display = "none";
        }

        if (prevBtn) {
          prevBtn.style.display = "none";
        }

        if (nextBtn) {
          nextBtn.style.display = "none";
        }
      }

      updateImage();
    });
  }

  //  필터 열고 닫기 기능
  function toggleFeedFilter() {
    if (!filterInner) return;

    const isClosed =
      filterInner.style.display === "none" || filterInner.style.display === "";

    if (isClosed) {
      filterInner.style.display = "flex";

      if (filterBar) {
        filterBar.classList.remove("is-closed");
      }

      if (filterToggleIcon) {
        filterToggleIcon.innerText = "접기 －";
      }
    } else {
      filterInner.style.display = "none";

      if (filterBar) {
        filterBar.classList.add("is-closed");
      }

      if (filterToggleIcon) {
        filterToggleIcon.innerText = "펼치기 ＋";
      }
    }
  }

  // 해시태그 선택 시 해당 피드만 보여주는 기능
  function applyFilter(selectedTag) {
    const feedCards = document.querySelectorAll(".feed-card");
    let visibleCount = 0;

    feedCards.forEach(function (card) {
      const cardTags = card.dataset.tags || "";

      if (selectedTag === "all" || cardTags.includes(selectedTag)) {
        card.style.display = "block";
        visibleCount++;
      } else {
        card.style.display = "none";
      }
    });

    if (feedEmpty) {
      feedEmpty.style.display = visibleCount === 0 ? "block" : "none";
    }
  }


  //좋아요 기능
  function initLikeButtons() {
    document.querySelectorAll(".feed-like-btn").forEach(function (button) {
      const icon = button.querySelector(".feed-like-icon");
      const countText = button.querySelector(".feed-like-count");
      const card = button.closest(".feed-card");

      if (!card) return;

      let isRequesting = false;

      button.addEventListener("click", function (event) {
        event.preventDefault();
        event.stopPropagation();

        if (isRequesting) return;

        const feedId = card.dataset.feedId;
        const liked = button.dataset.liked === "true";

        let count = Number(button.dataset.likeCount || 0);

        if (Number.isNaN(count)) {
          count = 0;
        }

        const newLiked = !liked;
        const newCount = newLiked ? count + 1 : Math.max(count - 1, 0);

        applyLikeUI(newLiked, newCount);

        isRequesting = true;

        fetch("/feed/" + feedId + "/like?liked=" + liked, {
          method: "POST",
          headers: getCsrfHeaders(),
        })
          .then(function (response) {
            return response.text();
          })
          .then(function (likeCountText) {
            const serverCount = Number(likeCountText);

            if (!Number.isNaN(serverCount)) {
              applyLikeUI(newLiked, serverCount);
            }
          })
          .catch(function (error) {
            console.log(error);
            applyLikeUI(liked, count);
          })
          .finally(function () {
            isRequesting = false;
          });
      });

      function applyLikeUI(isLiked, likeCount) {
        button.dataset.liked = isLiked ? "true" : "false";
        button.dataset.likeCount = likeCount;

        if (isLiked) {
          button.classList.add("is-liked");

          if (icon) {
            icon.textContent = "♥";
            icon.style.color = "#111";
          }
        } else {
          button.classList.remove("is-liked");

          if (icon) {
            icon.textContent = "♡";
            icon.style.color = "";
          }
        }

        if (countText) {
          countText.textContent = likeCount;
        }
      }
    });
  }

 
  // 댓글 기능 전체
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
          const moreBtn = event.target.closest(".comment-more-btn");
          const editBtn = event.target.closest(".comment-edit-btn");
          const deleteBtn = event.target.closest(".comment-delete-btn");
          const commentBubble = event.target.closest(
            ".feed-comment-bubble, .comment-child-bubble",
          );

          // 멘션 클릭 시 원댓글 위치로 이동
          if (mentionEl) {
            event.stopPropagation();
            moveToMentionTarget(mentionEl);
            return;
          }

          // 댓글 더보기 메뉴 열기
          if (moreBtn) {
            event.stopPropagation();

            const menuWrap = moreBtn.closest(".comment-more-wrap");
            const menu = menuWrap.querySelector(".comment-more-menu");

            document
              .querySelectorAll(".comment-more-menu")
              .forEach(function (otherMenu) {
                if (otherMenu !== menu) {
                  otherMenu.classList.remove("is-open");
                }
              });

            menu.classList.toggle("is-open");
            return;
          }

          // 댓글 수정
          if (editBtn) {
            event.stopPropagation();

            const commentItem = editBtn.closest("[data-reply-id]");
            const replyId = commentItem.dataset.replyId;
            const contentEl = commentItem.querySelector(
              ".feed-comment-content, .comment-child-content",
            );

            const oldHtml = contentEl.innerHTML;
            const oldText = getPureContentText(contentEl);

            contentEl.innerHTML = "";

            const input = document.createElement("input");
            input.type = "text";
            input.className = "comment-edit-input";
            input.value = oldText;

            const saveBtn = document.createElement("button");
            saveBtn.type = "button";
            saveBtn.className = "comment-save-btn";
            saveBtn.textContent = "저장";

            const cancelBtn = document.createElement("button");
            cancelBtn.type = "button";
            cancelBtn.className = "comment-cancel-btn";
            cancelBtn.textContent = "취소";

            contentEl.appendChild(input);
            contentEl.appendChild(saveBtn);
            contentEl.appendChild(cancelBtn);

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
                contentEl.textContent = newText;
              });
            });

            cancelBtn.addEventListener("click", function (e) {
              e.stopPropagation();
              contentEl.innerHTML = oldHtml;
            });

            return;
          }

          // 댓글 삭제
          if (deleteBtn) {
            event.stopPropagation();

            const commentItem = deleteBtn.closest("[data-reply-id]");
            const replyId = commentItem.dataset.replyId;

            fetch("/feed/reply/" + replyId + "/delete", {
              method: "POST",
              headers: getCsrfHeaders(),
            }).then(function () {
              const contentEl = commentItem.querySelector(
                ".feed-comment-content, .comment-child-content",
              );

              if (contentEl) {
                contentEl.textContent = "사용자가 삭제한 댓글입니다.";
                contentEl.classList.add("deleted-comment-text");
              }

              const moreWrap = commentItem.querySelector(".comment-more-wrap");

              if (moreWrap) {
                moreWrap.remove();
              }
            });

            return;
          }

          // 댓글 클릭 시 답글 대상 설정
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

      // 로그인한 사용자가 쓴 댓글에만 수정/삭제 메뉴 보이게 처리
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

      // 답글 여러 개일 때 접기 , 펼치기 버튼 만들기
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
      // 답글이면 @닉네임 멘션 같이 붙여줌
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

      // 수정할 때 멘션 제외하고 순수 댓글 내용만 가져오기
      function getPureContentText(contentEl) {
        if (!contentEl) return "";

        const clone = contentEl.cloneNode(true);
        const mention = clone.querySelector(".comment-mention");

        if (mention) {
          mention.remove();
        }

        return clone.textContent.trim();
      }

      // @멘션 클릭하면 원댓글 위치로 이동
      function moveToMentionTarget(mentionEl) {
        if (!commentList || !mentionEl) return;

        const targetReplyId = mentionEl.dataset.targetReplyId;

        if (!targetReplyId) return;

        const targetItem = commentList.querySelector(
          '[data-reply-id="' + targetReplyId + '"]',
        );

        if (!targetItem) return;

        const childList = targetItem.closest(".comment-child-list");

        if (childList) {
          childList.style.setProperty("display", "block", "important");

          const parentComment = childList.closest(".feed-comment-item");
          const toggleBtn = parentComment
            ? parentComment.querySelector(".comment-child-toggle-btn")
            : null;

          if (toggleBtn) {
            toggleBtn.classList.add("is-open");
            toggleBtn.textContent = "답글 숨기기";
          }
        }

        targetItem.scrollIntoView({
          behavior: "smooth",
          block: "center",
        });

        targetItem.classList.add("comment-focus-highlight");

        setTimeout(function () {
          targetItem.classList.remove("comment-focus-highlight");
        }, 1200);
      }

      // 댓글 수정/삭제 메뉴 생성
      function createCommentMoreWrap() {
        const moreWrap = document.createElement("div");
        moreWrap.className = "comment-more-wrap";

        const moreBtn = document.createElement("button");
        moreBtn.type = "button";
        moreBtn.className = "comment-more-btn";
        moreBtn.textContent = "···";

        const menu = document.createElement("div");
        menu.className = "comment-more-menu";

        const editBtn = document.createElement("button");
        editBtn.type = "button";
        editBtn.className = "comment-menu-item comment-edit-btn";
        editBtn.textContent = "수정하기";

        const deleteBtn = document.createElement("button");
        deleteBtn.type = "button";
        deleteBtn.className = "comment-menu-item danger comment-delete-btn";
        deleteBtn.textContent = "삭제하기";

        menu.appendChild(editBtn);
        menu.appendChild(deleteBtn);

        moreWrap.appendChild(moreBtn);
        moreWrap.appendChild(menu);

        return moreWrap;
      }

      // 댓글 모두 보기 / 접기 버튼 문구 변경
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


  //피드 안의 해시태그 버튼 클릭 기능
  function initHashtagButtons() {
    document.querySelectorAll(".feed-hashtag-btn").forEach(function (button) {
      button.addEventListener("click", function () {
        const selectedTag = button.dataset.tag;

        document.querySelectorAll(".feed-tag-btn").forEach(function (tagBtn) {
          tagBtn.classList.remove("active");

          if (tagBtn.dataset.tag === selectedTag) {
            tagBtn.classList.add("active");
          }
        });

        applyFilter(selectedTag);

        if (filterInner) {
          filterInner.style.display = "flex";
        }

        if (filterBar) {
          filterBar.classList.remove("is-closed");
        }

        if (filterToggleIcon) {
          filterToggleIcon.innerText = "접기 －";
        }
      });
    });
  }


  //  피드 작성자 본인 확인
  function isOwner(card) {
    if (!card) return false;

    const loginUsername = getLoginUsername();

    const feedUsername = card.dataset.username
      ? card.dataset.username.trim()
      : "";

    if (!loginUsername || !feedUsername) {
      return false;
    }

    return loginUsername === feedUsername;
  }

  //  수정/삭제 버튼

  function initOwnerMoreMenus() {
    document.querySelectorAll(".feed-card").forEach(function (card) {
      const moreWrap = card.querySelector(".feed-more-wrap");

      if (!moreWrap) return;

      if (isOwner(card)) {
        moreWrap.style.display = "block";
      } else {
        moreWrap.style.display = "none";
      }
    });
  }

 
  // 피드 더보기 메뉴
 

  function initMoreMenu() {
    document.querySelectorAll(".feed-more-btn").forEach(function (button) {
      button.addEventListener("click", function (event) {
        event.stopPropagation();

        const card = button.closest(".feed-card");

        if (!isOwner(card)) {
          return;
        }

        const currentMenu = button
          .closest(".feed-more-wrap")
          .querySelector(".feed-more-menu");

        document.querySelectorAll(".feed-more-menu").forEach(function (menu) {
          if (menu !== currentMenu) {
            menu.classList.remove("is-open");
          }
        });

        currentMenu.classList.toggle("is-open");
      });
    });

    document.addEventListener("click", function () {
      document.querySelectorAll(".feed-more-menu").forEach(function (menu) {
        menu.classList.remove("is-open");
      });

      document.querySelectorAll(".comment-more-menu").forEach(function (menu) {
        menu.classList.remove("is-open");
      });

      document
        .querySelectorAll(
          ".feed-comment-item.menu-open, .comment-child-item.menu-open",
        )
        .forEach(function (item) {
          item.classList.remove("menu-open");
        });
    });

    document.querySelectorAll(".feed-menu-item").forEach(function (btn) {
      btn.addEventListener("click", function (event) {
        event.stopPropagation();

        const card = btn.closest(".feed-card");
        const feedId = card ? card.dataset.feedId : null;

        if (btn.classList.contains("danger")) {
          location.href = "/feed/" + feedId + "/delete";
          return;
        }

        if (btn.classList.contains("edit-btn")) {
          location.href = "/feed/" + feedId + "/edit";
        }
      });
    });
  }

  // 댓글 수정/삭제 플로팅 메뉴
  function initFloatingCommentMenu() {
    const style = document.createElement("style");

    style.textContent = `
      .floating-comment-menu {
        position: fixed !important;
        display: none;
        min-width: 96px !important;
        background: #ffffff !important;
        border: 1px solid #ececec !important;
        border-radius: 10px !important;
        box-shadow: 0 10px 24px rgba(0, 0, 0, 0.18) !important;
        padding: 6px !important;
        z-index: 2147483647 !important;
      }

      .floating-comment-menu.is-open {
        display: block !important;
      }

      .floating-comment-menu button {
        display: block !important;
        width: 100% !important;
        padding: 8px 10px !important;
        border: 0 !important;
        background: #fff !important;
        text-align: left !important;
        font-size: 13px !important;
        cursor: pointer !important;
        border-radius: 7px !important;
        color: #222 !important;
      }

      .floating-comment-menu button:hover {
        background: #f7f7f7 !important;
      }

      .floating-comment-menu button.danger {
        color: #e44747 !important;
      }
    `;

    document.head.appendChild(style);

    const floatingMenu = document.createElement("div");
    floatingMenu.className = "floating-comment-menu";

    const editButton = document.createElement("button");
    editButton.type = "button";
    editButton.textContent = "수정하기";
    editButton.className = "floating-comment-edit-btn";

    const deleteButton = document.createElement("button");
    deleteButton.type = "button";
    deleteButton.textContent = "삭제하기";
    deleteButton.className = "floating-comment-delete-btn danger";

    floatingMenu.appendChild(editButton);
    floatingMenu.appendChild(deleteButton);
    document.body.appendChild(floatingMenu);

    let currentMoreWrap = null;

    function closeFloatingMenu() {
      floatingMenu.classList.remove("is-open");
      currentMoreWrap = null;
    }

    document.addEventListener(
      "click",
      function (event) {
        const moreBtn = event.target.closest(".comment-more-btn");

        if (moreBtn) {
          event.preventDefault();
          event.stopPropagation();
          event.stopImmediatePropagation();

          const moreWrap = moreBtn.closest(".comment-more-wrap");
          const originalMenu = moreWrap
            ? moreWrap.querySelector(".comment-more-menu")
            : null;

          if (!moreWrap || !originalMenu) return;

          const rect = moreBtn.getBoundingClientRect();

          floatingMenu.style.top = rect.bottom + 6 + "px";
          floatingMenu.style.left = "auto";
          floatingMenu.style.right = window.innerWidth - rect.right + "px";

          currentMoreWrap = moreWrap;

          document
            .querySelectorAll(".comment-more-menu")
            .forEach(function (menu) {
              menu.classList.remove("is-open");
            });

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

    editButton.addEventListener("click", function (event) {
      event.preventDefault();
      event.stopPropagation();

      if (!currentMoreWrap) return;

      const originalEditBtn =
        currentMoreWrap.querySelector(".comment-edit-btn");

      if (originalEditBtn) {
        originalEditBtn.click();
      }

      closeFloatingMenu();
    });

    deleteButton.addEventListener("click", function (event) {
      event.preventDefault();
      event.stopPropagation();

      if (!currentMoreWrap) return;

      const originalDeleteBtn = currentMoreWrap.querySelector(
        ".comment-delete-btn",
      );

      if (originalDeleteBtn) {
        originalDeleteBtn.click();
      }

      closeFloatingMenu();
    });

    window.addEventListener("scroll", closeFloatingMenu);
    window.addEventListener("resize", closeFloatingMenu);
  }

  // 상단 해시태그 필터 바 이벤트
 

  if (filterSummary) {
    filterSummary.addEventListener("click", toggleFeedFilter);

    filterSummary.addEventListener("keydown", function (event) {
      if (event.key === "Enter" || event.key === " ") {
        event.preventDefault();
        toggleFeedFilter();
      }
    });
  }

  if (filterInner) {
    filterInner.addEventListener("click", function (event) {
      if (!event.target.classList.contains("feed-tag-btn")) return;

      document.querySelectorAll(".feed-tag-btn").forEach(function (button) {
        button.classList.remove("active");
      });

      event.target.classList.add("active");
      applyFilter(event.target.dataset.tag);
    });
  }


  // 페이지 로딩 후 실행되는 기능들
  removeDuplicateTagButtons();
  renderFeedContentAndLink();
  renderFeedImages();
  initLikeButtons();
  initCommentArea();
  initHashtagButtons();
  initFeedSortSelect();
  initOwnerMoreMenus();
  initMoreMenu();
  initFloatingCommentMenu();
});