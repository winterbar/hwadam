let existingFiles = []; //기존에 있는 파일 담는 배열
let selectedFiles = []; //새로 넣을 파일 담는 배열
let deletedFileIds = [] //삭제될 파일 아이디 담는 배열

        const fileInput = document.getElementById("file-input");
        const dropZone = document.getElementById("dropZone");
        const previewList = document.getElementById("previewList");
        const fileList = document.getElementById("fileList");
        const boardId = previewList.dataset.id;

        document.addEventListener("DOMContentLoaded", function(){

            // 기존 파일 렌더링
            fetch(`/api/board/${boardId}/render-file`)
            .then(res => res.json())
            .then(data=>{

                existingFiles = data.files;

                renderFileItems(); //렌더링 함수 사용.
     
            });
            
        });

        // 파일 선택
        fileInput.addEventListener("change", function(e) {

            addFiles(e.target.files);
            fileInput.value = ""; //파일 재선택이 가능하도록

        });

        // 드래그 오버
        dropZone.addEventListener("dragover", function(e) {

            e.preventDefault();
            dropZone.classList.add("dragover");

        });

        // 드래그 떠남
        dropZone.addEventListener("dragleave", function() {

            dropZone.classList.remove("dragover");

        });

        // 드롭
        dropZone.addEventListener("drop", function(e) {

            e.preventDefault();

            dropZone.classList.remove("dragover");

            addFiles(e.dataTransfer.files);

        });

        // 파일 추가
        function addFiles(files) {

            for (let file of files) {

                if(!file.type.startsWith("image/")){
                    alert("이미지만 업로드 가능합니다!");
                    continue;
                }
                selectedFiles.push(file);
            }
            renderFileItems();
        }

        // 새로운 파일 화면에 같이 출력
        function renderFileItems() {

            previewList.innerHTML = "";

            // 기존 파일 렌더링
            existingFiles.forEach((file, index) => {

                const imgUrl = '/download/'+file.savedName;

                const div = document.createElement("div");

                div.classList.add("preview-item");

                div.innerHTML = `
                    <img src="${imgUrl}">
                    <button type="button"
                            class="remove-btn">
                        x
                    </button>
                `;

                // 파일 삭제
                div.querySelector(".remove-btn").addEventListener("click", function(){
                    deletedFileIds.push(file.fileId);
                    existingFiles = existingFiles.filter(f => f!== file);
                    div.remove();
                    // 또 패치 써야한다고...?
                });
                
                previewList.appendChild(div);

            });

            selectedFiles.forEach((file) => {

                    const imgUrl = URL.createObjectURL(file);

                    const div = document.createElement("div");

                    div.classList.add("preview-item");

                    div.innerHTML = `
                        <img src="${imgUrl}">
                        <button type="button"
                                class="remove-btn">
                            x
                        </button>
                    `;

                    // 파일 삭제
                    div.querySelector(".remove-btn").addEventListener("click", function(){
                    selectedFiles = selectedFiles.filter(f => f!== file);
                    div.remove();
                });
                previewList.appendChild(div);
            });

        }

        const updateForm = document.querySelector("form[action*='/community/update']");

        if(updateForm){

            updateForm.addEventListener("submit", function(e){

                e.preventDefault();

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

                const fileInputForUpdate = document.getElementById("file-input");
                fileInputForUpdate.files = dataTransfer.files;

                updateForm.submit();

            });

        }
        