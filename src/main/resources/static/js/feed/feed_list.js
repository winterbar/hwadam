document.addEventListener("DOMContentLoaded", function () {
  const filterBar = document.getElementById("feed-filter-bar");
  const filterSummary = document.getElementById("feed-filter-summary");
  const filterToggleIcon = document.getElementById("feed-filter-toggle-icon");
  const filterInner = document.getElementById("feed-filter-inner");
  const feedEmpty = document.getElementById("feed-empty");

  // 현재 로그인한 사용자 아이디
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

  // 중복 태그 제거
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

  function renderFeedContentAndLink() {
    const feedCards = document.querySelectorAll(".feed-card");

    feedCards.forEach(function (card) {
      const captionTextEl = card.querySelector(".js-caption-text");
      const link = card.querySelector(".feed-share-link");

      if (!captionTextEl || !link) return;

      const rawContent = captionTextEl.textContent
        ? captionTextEl.textContent
        : card.dataset.content
          ? card.dataset.content
          : "";

      link.style.display = "none";

      if (!rawContent.trim()) return;

      const urlRegex = /(https?:\/\/[^\s]+)/;
      const urlMatch = rawContent.match(urlRegex);

      let contentText = rawContent;

      if (urlMatch) {
        const linkText = urlMatch[0];

        contentText = rawContent.replace(linkText, "").trim();
        contentText = contentText.replace(/,\s*$/, "").trim();

        link.href = linkText;
        link.style.display = "inline-flex";
      }

      captionTextEl.textContent = contentText.replace(/,\s*$/, "").trim();
    });
  }

  function renderFeedImages() {
    const feedCards = document.querySelectorAll(".feed-card");

    feedCards.forEach(function (card) {
      const imageEl = card.querySelector(".js-feed-image");
      const placeholder = card.querySelector(".js-feed-placeholder");
      const badge = card.querySelector(".js-photo-badge");
      const prevBtn = card.querySelector(".feed-photo-prev");
      const nextBtn = card.querySelector(".feed-photo-next");

      const rawImages = card.dataset.images ? card.dataset.images.trim() : "";

      const images = rawImages
        .split(",")
        .map(function (name) {
          return name.trim();
        })
        .filter(function (name) {
          return name !== "" && name !== "null" && name !== "undefined";
        });

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
          badge.style.display = "flex";
          badge.textContent = currentIndex + 1 + " / " + images.length;
        }

        if (prevBtn) {
          prevBtn.style.display =
            images.length > 1 && currentIndex > 0 ? "flex" : "none";
        }

        if (nextBtn) {
          nextBtn.style.display =
            images.length > 1 && currentIndex < images.length - 1
              ? "flex"
              : "none";
        }
      }

      if (prevBtn) {
        prevBtn.addEventListener("click", function () {
          if (images.length <= 1) return;
          if (currentIndex <= 0) return;

          currentIndex = currentIndex - 1;
          updateImage();
        });
      }

      if (nextBtn) {
        nextBtn.addEventListener("click", function () {
          if (images.length <= 1) return;
          if (currentIndex >= images.length - 1) return;

          currentIndex = currentIndex + 1;
          updateImage();
        });
      }

      updateImage();
    });
  }

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

  function initLikeButtons() {
    document.querySelectorAll(".feed-like-btn").forEach(function (button) {
      const icon = button.querySelector(".feed-like-icon");
      const card = button.closest(".feed-card");
      const countText = card ? card.querySelector(".feed-like-count") : null;

      let isRequesting = false;

      let liked = button.dataset.liked === "true";
      let count = Number(button.dataset.likeCount || 0);

      if (Number.isNaN(count)) {
        count = 0;
      }

      applyLikeUI(liked, count);

      button.addEventListener("click", function () {
        if (isRequesting) return;

        const loginUsername = getLoginUsername();
        const feedId = card ? card.dataset.feedId : null;
        const currentLiked = button.dataset.liked === "true";

        isRequesting = true;

        fetch("/feed/" + feedId + "/like?liked=" + currentLiked, {
          method: "POST",
          headers: getCsrfHeaders(),
        })
          .then(function (response) {
            return response.text();
          })
          .then(function (likeCountText) {
            const newLikeCount = Number(likeCountText);
            const newLiked = !currentLiked;

            applyLikeUI(newLiked, newLikeCount);
          })
          .catch(function (error) {
            console.log(error);
          })
          .finally(function () {
            isRequesting = false;
          });
      });

      function applyLikeUI(isLiked, likeCount) {
        if (Number.isNaN(likeCount)) {
          likeCount = 0;
        }

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

      if (commentToggleBtn && commentBox) {
        commentToggleBtn.addEventListener("click", function () {
          const isOpen = commentBox.style.display === "block";
          commentBox.style.display = isOpen ? "none" : "block";

          if (!isOpen && commentInput) {
            commentInput.focus();
          }
        });
      }

      if (commentListBtn && commentList) {
        commentListBtn.addEventListener("click", function () {
          const isOpen = commentList.style.display === "block";

          commentList.style.display = isOpen ? "none" : "block";
          updateCommentCountText(!isOpen);
        });
      }

      if (replyCancelBtn) {
        replyCancelBtn.addEventListener("click", function () {
          clearReplyTarget();
        });
      }

      if (commentForm && commentInput) {
        commentForm.addEventListener("submit", function (event) {
          event.preventDefault();

          const loginUsername = getLoginUsername();
          const value = commentInput.value.trim();
          const action = commentForm.getAttribute("action");
          const formData = new FormData(commentForm);
          const submitContent = commentInput.value.trim();

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

      if (commentList) {
        commentList.addEventListener("click", function (event) {
          const mentionEl = event.target.closest(".comment-mention");
          const moreBtn = event.target.closest(".comment-more-btn");
          const editBtn = event.target.closest(".comment-edit-btn");
          const deleteBtn = event.target.closest(".comment-delete-btn");
          const commentBubble = event.target.closest(
            ".feed-comment-bubble, .comment-child-bubble",
          );

          if (mentionEl) {
            event.stopPropagation();
            moveToMentionTarget(mentionEl);
            return;
          }

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

          if (deleteBtn) {
            event.stopPropagation();

            const commentItem = deleteBtn.closest("[data-reply-id]");
            const replyId = commentItem.dataset.replyId;

            if (confirm("댓글을 삭제하시겠습니까?")) {
              fetch("/feed/reply/" + replyId + "/delete", {
                method: "POST",
                headers: getCsrfHeaders(),
              }).then(function () {
                commentItem.remove();
              });
            }

            return;
          }

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
        avatar.textContent = writerName ? writerName.substring(0, 1) : "회";

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
        avatar.textContent = writerName ? writerName.substring(0, 1) : "회";

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

      function getPureContentText(contentEl) {
        if (!contentEl) return "";

        const clone = contentEl.cloneNode(true);
        const mention = clone.querySelector(".comment-mention");

        if (mention) {
          mention.remove();
        }

        return clone.textContent.trim();
      }

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

      function handleCommentEdit(editBtn) {
        const commentItem = editBtn.closest("[data-reply-id]");

        if (!commentItem) return;

        const replyId = commentItem.dataset.replyId;
        const contentEl = commentItem.querySelector(
          ".feed-comment-content, .comment-child-content",
        );
        if (!contentEl) return;

        const currentContent = getPureContentText(contentEl);
        const newContent = prompt("댓글을 수정해주세요.", currentContent);

        if (newContent === null) return;

        const trimmedContent = newContent.trim();

        if (!trimmedContent) {
          alert("댓글 내용을 입력해주세요.");
          return;
        }

        const formData = new FormData();
        formData.append("replyContent", trimmedContent);

        fetch("/feed/reply/" + replyId + "/edit", {
          method: "POST",
          headers: getCsrfHeaders(),
          body: formData,
        })
          .then(function (response) {
            const mentionEl = contentEl.querySelector(".comment-mention");
            const parentNickname = mentionEl
              ? mentionEl.textContent.replace("@", "").trim()
              : "";
            const parentReplyId = mentionEl
              ? mentionEl.dataset.targetReplyId
              : "";

            renderReplyContent(
              contentEl,
              trimmedContent,
              parentNickname,
              parentReplyId,
            );

            document
              .querySelectorAll(".comment-more-menu")
              .forEach(function (menu) {
                menu.classList.remove("is-open");
              });

            document
              .querySelectorAll(
                ".feed-comment-item.menu-open, .comment-child-item.menu-open",
              )
              .forEach(function (item) {
                item.classList.remove("menu-open");
              });
          })
          .catch(function (error) {
            console.log(error);
          });
      }

      function handleCommentDelete(deleteBtn) {
        const commentItem = deleteBtn.closest("[data-reply-id]");

        if (!commentItem) return;

        const replyId = commentItem.dataset.replyId;
        const isDelete = confirm("댓글을 삭제하시겠습니까?");

        if (!isDelete) return;

        fetch("/feed/reply/" + replyId + "/delete", {
          method: "POST",
          headers: getCsrfHeaders(),
        })
          .then(function (response) {
            commentCount = Math.max(commentCount - 1, 0);

            commentItem.remove();

            if (commentListBtn) {
              commentListBtn.dataset.commentCount = commentCount;
            }

            updateCommentCountText(true);

            if (commentCount === 0 && commentList) {
              const emptyItem = document.createElement("div");
              emptyItem.className = "feed-comment-item feed-comment-empty";

              const emptyText = document.createElement("span");
              emptyText.textContent = "아직 등록된 댓글이 없습니다.";

              emptyItem.appendChild(emptyText);
              commentList.appendChild(emptyItem);
            }
          })
          .catch(function (error) {
            console.log(error);
          });
      }

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
          const isDelete = confirm("해당 피드를 삭제하시겠습니까?");

          if (!isDelete) {
            return;
          }

          location.href = "/feed/" + feedId + "/delete";
          return;
        }

        if (btn.classList.contains("edit-btn")) {
          location.href = "/feed/" + feedId + "/edit";
        }
      });
    });
  }

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
  removeDuplicateTagButtons();
  renderFeedContentAndLink();
  renderFeedImages();
  initLikeButtons();
  initCommentArea();
  initHashtagButtons();
  initOwnerMoreMenus();
  initMoreMenu();
  initFloatingCommentMenu();
});
