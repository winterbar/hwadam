document.addEventListener("DOMContentLoaded", function () {
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

});