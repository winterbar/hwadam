function confirmDelete(boardId) {
        // 1. 안내창 띄우기 
        if (confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
            // 2. 예(확인)을 누르면 삭제를 처리할 벡엔드 URL로 요청을 보냄
            // 폼을 동적으로 생성해서 Post로 보내거나, 아래처럼 비동기 fetch를 사용
            fetch(`/review/delete/${boardId}`,{
                method: "POST"
            })
            .then(response => {
                if(response.ok) {
                    alert("게시글이 성공적으로 삭제되었습니다.")
                    location.href = "/review"; // 삭제 후 목록으로 이동
                } else {
                    alret("삭제 권한이 없거나 오류가 발생했습니다.");
                }
            })
            .catch(error => console.error("Error:", error));
        }
    }