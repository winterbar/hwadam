document.addEventListener("DOMContentLoaded", function () {
<<<<<<< HEAD
  function getCsrfHeaders() {
    const csrfToken = document.querySelector('meta[name="_csrf"]');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]');

    const headers = {};

    if (csrfToken && csrfHeader) {
      headers[csrfHeader.getAttribute("content")] =
        csrfToken.getAttribute("content");
=======

  const loginUsername =
    document.body.dataset.loginUsername
      ? document.body.dataset.loginUsername.trim()
      : "";

  const isLoggedIn =
    loginUsername !== "" &&
    loginUsername !== "anonymousUser";


  // CSRF 헤더
  function getCsrfHeaders() {
    const tokenMeta =
      document.querySelector('meta[name="_csrf"]');

    const headerMeta =
      document.querySelector('meta[name="_csrf_header"]');

    const headers = {};

    if (tokenMeta && headerMeta) {
      headers[headerMeta.content] =
        tokenMeta.content;
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e
    }

    return headers;
  }
<<<<<<< HEAD
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
            icon.textContent = "❤️";
            icon.style.color = "#111";
          }
        } else {
          button.classList.remove("is-liked");

          if (icon) {
            icon.textContent = "🤍";
            icon.style.color = "";
          }
        }

        if (countText) {
          countText.textContent = likeCount;
        }
      }
    });
  }
  
  initLikeButtons();
=======


  // 사용자별 좋아요 저장 키
  function getLikeStorageKey(feedId) {
    return (
      "feed-like:" +
      loginUsername +
      ":" +
      feedId
    );
  }


  // 저장된 사용자별 좋아요 상태
  function getSavedLikeState(feedId) {
    if (!isLoggedIn) {
      return false;
    }

    const savedValue =
      localStorage.getItem(
        getLikeStorageKey(feedId)
      );

    if (savedValue === "true") {
      return true;
    }

    if (savedValue === "false") {
      return false;
    }

    /*
     * 현재 서버의 feed.liked가 사용자별로
     * 정확하지 않은 상황이므로 저장 기록이 없으면
     * 기본값을 false로 설정
     */
    return false;
  }


  // 사용자별 좋아요 상태 저장
  function saveLikeState(feedId, liked) {
    if (!isLoggedIn) {
      return;
    }

    localStorage.setItem(
      getLikeStorageKey(feedId),
      liked ? "true" : "false"
    );
  }


  // 좋아요 UI 변경
  function applyLikeUI(
    button,
    liked,
    likeCount
  ) {
    const icon =
      button.querySelector(".feed-like-icon");

    const countText =
      button.querySelector(".feed-like-count");

    button.dataset.liked =
      liked ? "true" : "false";

    button.dataset.likeCount =
      String(likeCount);

    if (liked) {
      button.classList.add("is-liked");

      if (icon) {
        icon.textContent = "❤️";
      }
    } else {
      button.classList.remove("is-liked");

      if (icon) {
        icon.textContent = "🤍";
      }
    }

    if (countText) {
      countText.textContent =
        String(likeCount);
    }
  }


  // 좋아요 버튼 초기화
  document
    .querySelectorAll(".feed-like-btn")
    .forEach(function (button) {

      if (
        button.dataset.likeInitialized ===
        "true"
      ) {
        return;
      }

      button.dataset.likeInitialized =
        "true";

      const card =
        button.closest(".feed-card");

      if (!card) {
        return;
      }

      const feedId =
        button.dataset.feedId ||
        card.dataset.feedId;

      if (!feedId) {
        return;
      }

      let currentCount =
        Number(
          button.dataset.likeCount || 0
        );

      if (Number.isNaN(currentCount)) {
        currentCount = 0;
      }

      const initialLiked =
        getSavedLikeState(feedId);

      applyLikeUI(
        button,
        initialLiked,
        currentCount
      );

      let isRequesting = false;


      button.addEventListener(
        "click",
        function (event) {
          event.preventDefault();
          event.stopPropagation();

          if (!isLoggedIn) {
            location.href = "/login";
            return;
          }

          if (isRequesting) {
            return;
          }

          const liked =
            button.dataset.liked ===
            "true";

          let previousCount =
            Number(
              button.dataset.likeCount || 0
            );

          if (
            Number.isNaN(previousCount)
          ) {
            previousCount = 0;
          }

          isRequesting = true;
          button.disabled = true;

          fetch(
            "/feed/" +
              encodeURIComponent(feedId) +
              "/like?liked=" +
              liked,
            {
              method: "POST",
              headers: getCsrfHeaders()
            }
          )
            .then(function (response) {
              if (
                response.status === 401 ||
                response.status === 403
              ) {
                location.href = "/login";

                throw new Error(
                  "로그인이 필요합니다."
                );
              }

              if (!response.ok) {
                throw new Error(
                  "좋아요 처리 실패"
                );
              }

              return response.text();
            })
            .then(function (responseText) {
              const newLiked = !liked;

              const serverCount =
                Number(responseText);

              let newCount;

              if (
                Number.isNaN(serverCount)
              ) {
                newCount = newLiked
                  ? previousCount + 1
                  : Math.max(
                      previousCount - 1,
                      0
                    );
              } else {
                newCount = serverCount;
              }

              saveLikeState(
                feedId,
                newLiked
              );

              applyLikeUI(
                button,
                newLiked,
                newCount
              );
            })
            .catch(function (error) {
              console.error(error);

              if (
                error.message !==
                "로그인이 필요합니다."
              ) {
                alert(
                  "좋아요 처리 중 오류가 발생했습니다."
                );
              }
            })
            .finally(function () {
              isRequesting = false;
              button.disabled = false;
            });
        }
      );
    });
    
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e

});