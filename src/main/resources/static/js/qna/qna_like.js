document.addEventListener("DOMContentLoaded", function () {

    const like = document.getElementById('like-icon');
    const likeCnt = document.getElementById('like-count');
    const boardId = like.dataset.id;

    if (boardId){
        fetch(`/api/board/${boardId}/like-check`, {method: 'GET'})
        .then(res=>res.json())
        .then(data=>{
            if (data.isLikeOn){
                like.textContent = '♥';
                like.classList.add('on-like');
            }
        })
        .catch(err => console.error("조회 실패", err));
    }

    like.addEventListener('click', function () {

        if(!boardId) return;

        fetch(`/api/board/${boardId}/toggle-like`, {method: 'POST'})
        .then(res => res.json())
        .then(data =>{

            console.log(data);

            if (data.isLikeOn){
                like.textContent = '♥';
                like.classList.add('on-like');
            }
            else{
                like.textContent = '♡';
                like.classList.remove('on-like');
            }

            if(data.likeCount !== undefined){
                likeCnt.textContent = data.likeCount;
            }
        })
        .catch(err => console.error("처리 실패:", err));
    });

});