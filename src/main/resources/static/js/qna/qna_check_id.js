<<<<<<< HEAD
document.addEventListener("DOMContentLoaded", function(){

    const buttonField = document.getElementById('action-field');
    const boardId = buttonField.dataset.id;

    fetch(`/api/board/${boardId}/check-id`, {method: 'GET'})
    .then(res =>res.json())
    .then(data =>{

        console.log(data.isOwner);
        console.log(typeof data.isOwner);

        if(data.isOwner === false){
            buttonField.style.display = "none";
        }

        console.log(buttonField.hidden);
        console.log(buttonField.outerHTML);
    })
=======
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
>>>>>>> 0d35ef02876eefeac7946681f4dd3e4770293f9e
});