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


// 피드 내용 출력
function renderFeedContentAndLink() {
  const feedCards = document.querySelectorAll(".feed-card");

  feedCards.forEach(function (card) {
    const captionTextEl = card.querySelector(".js-caption-text");
    const link = card.querySelector(".feed-share-link");

    if (!captionTextEl) return;

    const rawContent = captionTextEl.textContent || card.dataset.content || "";
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

  // 피드 이미지 출력
function renderFeedImages() {
  const feedCards = document.querySelectorAll(".feed-card");

  feedCards.forEach(function (card) {
    const imageEl = card.querySelector(".js-feed-image");
    const placeholder = card.querySelector(".js-feed-placeholder");
    const badge = card.querySelector(".js-photo-badge");
    const prevBtn = card.querySelector(".feed-photo-prev");
    const nextBtn = card.querySelector(".feed-photo-next");

    const rawImage = card.dataset.images ? card.dataset.images.trim() : "";

    const images = rawImage && rawImage !== "null" && rawImage !== "undefined"
      ? rawImage.split(",").map(function (img) {
          return img.trim();
        }).filter(Boolean)
      : [];

    let currentIndex = 0;

    function updateImage() {
      // 이미지가 없을 때
      if (images.length === 0) {
        if (imageEl) {
          imageEl.removeAttribute("src");
          imageEl.style.display = "none";
        }

        if (placeholder) placeholder.style.display = "flex";
        if (badge) badge.style.display = "none";
        if (prevBtn) prevBtn.style.display = "none";
        if (nextBtn) nextBtn.style.display = "none";

        return;
      }

      // 이미지가 있을 때
      if (imageEl) {
        imageEl.src = "/upload/feed/" + images[currentIndex];
        imageEl.style.display = "block";
      }

      if (placeholder) {
        placeholder.style.display = "none";
      }

      // 이미지가 여러 장이면 번호와 버튼 표시
      if (images.length > 1) {
        if (badge) {
          badge.textContent = (currentIndex + 1) + " / " + images.length;
          badge.style.display = "block";
        }

        if (prevBtn) {
          prevBtn.style.display = currentIndex === 0 ? "none" : "block";
        }

        if (nextBtn) {
          nextBtn.style.display = currentIndex === images.length - 1 ? "none" : "block";
        }
      } else {
        // 이미지가 한 장이면 버튼과 번호 숨김
        if (badge) badge.style.display = "none";
        if (prevBtn) prevBtn.style.display = "none";
        if (nextBtn) nextBtn.style.display = "none";
      }
    }

    if (nextBtn) {
      nextBtn.onclick = function (e) {
        e.preventDefault();

        if (currentIndex < images.length - 1) {
          currentIndex++;
          updateImage();
        }
      };
    }

    if (prevBtn) {
      prevBtn.onclick = function (e) {
        e.preventDefault();

        if (currentIndex > 0) {
          currentIndex--;
          updateImage();
        }
      };
    }

    updateImage();
  });
}

  // 필터 열고 닫기
function toggleFeedFilter() {
  if (!filterInner) return;

  const isClosed = filterInner.style.display === "none" || filterInner.style.display === "";

  if (isClosed) {
    filterInner.style.display = "flex";

    if (filterBar) {
      filterBar.classList.remove("is-closed");
    }

    if (filterToggleIcon) {
      filterToggleIcon.innerText = "접기 －";
    }

    return;
  }

  filterInner.style.display = "none";

  if (filterBar) {
    filterBar.classList.add("is-closed");
  }

  if (filterToggleIcon) {
    filterToggleIcon.innerText = "펼치기 ＋";
  }
}


  // 피드 정렬
function initFeedSortSelect() {
  const sortSelect = document.getElementById("feed-sort-select");
  const feedContainer = document.getElementById("feed-list-container");

  if (!sortSelect || !feedContainer) return;

  sortSelect.addEventListener("change", function () {
    const sortType = sortSelect.value;
    const feedCards = Array.from(feedContainer.querySelectorAll(".feed-card"));

    feedCards.sort(function (a, b) {
      const aFeedId = Number(a.dataset.feedId || 0);
      const bFeedId = Number(b.dataset.feedId || 0);

      const aLikeBtn = a.querySelector(".feed-like-btn");
      const bLikeBtn = b.querySelector(".feed-like-btn");

      const aLike = aLikeBtn ? Number(aLikeBtn.dataset.likeCount || 0) : 0;
      const bLike = bLikeBtn ? Number(bLikeBtn.dataset.likeCount || 0) : 0;

      const aCommentBtn = a.querySelector(".feed-comment-toggle-btn");
      const bCommentBtn = b.querySelector(".feed-comment-toggle-btn");

      const aComment = aCommentBtn ? Number(aCommentBtn.dataset.commentCount || 0) : 0;
      const bComment = bCommentBtn ? Number(bCommentBtn.dataset.commentCount || 0) : 0;

      if (sortType === "latest") {
        return bFeedId - aFeedId;
      }

      if (sortType === "oldest") {
        return aFeedId - bFeedId;
      }

      if (sortType === "like") {
        return bLike - aLike;
      }

      if (sortType === "comment") {
        return bComment - aComment;
      }

      return 0;
    });

    feedCards.forEach(function (card) {
      feedContainer.appendChild(card);
    });
  });
}

  

  // 피드 작성자인지 확인
function isOwner(card) {
  if (!card) return false;

  const loginUsername = getLoginUsername();
  const feedUsername = card.dataset.username ? card.dataset.username.trim() : "";

  if (!loginUsername || !feedUsername) {
    return false;
  }

  return loginUsername === feedUsername;
}

// 내 피드에만 더보기 버튼 보이기
function initOwnerMoreMenus() {
  document.querySelectorAll(".feed-card").forEach(function (card) {
    const moreWrap = card.querySelector(".feed-more-wrap");

    if (!moreWrap) return;

    moreWrap.style.display = isOwner(card) ? "block" : "none";
  });
}

// 피드 더보기 메뉴
function initMoreMenu() {
  document.querySelectorAll(".feed-more-btn").forEach(function (button) {
    button.addEventListener("click", function (event) {
      event.stopPropagation();

      const card = button.closest(".feed-card");

      if (!isOwner(card)) return;

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

document.querySelectorAll(".js-caption-text").forEach(function (caption) {
  caption.textContent = caption.textContent.trim();
});

renderFeedContentAndLink();
renderFeedImages();
initFeedSortSelect();
initOwnerMoreMenus();
initMoreMenu();
});