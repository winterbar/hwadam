const updateForm = document.querySelector("form[action*='/board/qna/update']");

updateForm.addEventListener("submit", function(e){

    // 폼이 제출되기 직전에 삭제할 파일 ID들을 hidden input으로
    deletedFileIds.forEach(id=>{
        const hiddenInput = document.createElement("input");
        hiddenInput.type = "hidden";
        hiddenInput.name = "deletedFileIds";
        hiddenInput.value = id;

        updateForm.appendChild(hiddenInput);
    });

    const dataTransfer = new DataTransfer();

    selectedFiles.forEach(file => {
        dataTransfer.items.add(file);
    });

    fileInputForUpdate.files = dataTransfer.files;

});
