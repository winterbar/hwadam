let selectedFiles = [];

        const fileInput = document.getElementById("file-input");
        const dropZone = document.getElementById("dropZone");
        const previewList = document.getElementById("previewList");
        const fileList = document.getElementById("fileList");

        // 파일 선택
        fileInput.addEventListener("change", function(e) {

            addFiles(e.target.files);

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

                selectedFiles.push(file);

            }

            syncInput();
            renderFiles();
        }

        // input 동기화
        function syncInput() {

            const dataTransfer = new DataTransfer();

            selectedFiles.forEach(file => {
                dataTransfer.items.add(file);
            });

            fileInput.files = dataTransfer.files;
        }

        // 화면 출력
        function renderFiles() {

            previewList.innerHTML = "";
            fileList.innerHTML = "";

            selectedFiles.forEach((file, index) => {

                // 이미지 파일
                if (file.type.startsWith("image/")) {

                    const imgUrl = URL.createObjectURL(file);

                    const div = document.createElement("div");

                    div.classList.add("preview-item");

                    div.innerHTML = `
                        <img src="${imgUrl}">
                        <button type="button"
                                class="remove-btn"
                                onclick="removeFile(${index})">
                            x
                        </button>
                    `;

                    previewList.appendChild(div);

                } else {

                    // 일반 파일

                    // const div = document.createElement("div");

                    // div.classList.add("file-item");

                    // div.innerHTML = `
                        // <span class="file-name">
                            // ${file.name}
                        // </span>

                        // <button type="button"
                                // class="file-remove-btn"
                                // onclick="removeFile(${index})">

                            // 삭제

                        // </button>
                    // `;

                    // fileList.appendChild(div);

                    alert("이미지를 올려주세요!");
                }

            });

        }

        // 삭제
        function removeFile(index) {

            selectedFiles.splice(index, 1);

            syncInput();
            renderFiles();

        }