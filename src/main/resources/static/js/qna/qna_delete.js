function deleteBoard(id){
        const delBoard = confirm("정말 삭제하시겠습니까?");
        if(delBoard){
            location.href='/board/qna/delete/'+id;
        }
    }