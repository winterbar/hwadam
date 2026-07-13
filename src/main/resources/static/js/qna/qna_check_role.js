document.addEventListener("DOMContentLoaded", ()=>{

    const noticeOp = document.getElementById('notice-op');

    // 콘텐츠가 로드되면...
    fetch(`/api/board/check-role`)
    .then(res=>res.json())
    .then(data=>{

        if (!data.isAdmin){
            noticeOp.hidden = true;
        }
    })

});