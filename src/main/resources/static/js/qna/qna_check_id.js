document.addEventListener("DOMContentLoaded", function () {

    const buttonField = document.getElementById('action-field');
    const boardId = buttonField.dataset.id;
    const commentArea = document.getElementById('comment-content');
    const commentBtn = document.getElementById('comment-submit');
    

    fetch(`/api/board/${boardId}/check-id`, { method: 'GET' })
        .then(res => res.json())
        .then(data => {
            if (data.isOwner === false) {
                buttonField.style.display = "none";
            }
            if (data.isLogOn === false){
                commentArea.value="로그인하시고 댓글을 작성해보세요!"
                commentArea.disabled = true;
            }
        })
});