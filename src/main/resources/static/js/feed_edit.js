document.addEventListener('DOMContentLoaded', function () {
    renderEditFeedContentAndLink();
    initEditImageArea();

    function renderEditFeedContentAndLink() {
        const form = document.querySelector('form');

        if (!form) return;

        const rawContent = form.dataset.content
            ? form.dataset.content.trim()
            : '';

        const contentInput = document.getElementById('editFeedContent');
        const linkInput = document.getElementById('editInfoLink');

        if (!contentInput || !linkInput) return;
        if (!rawContent) return;

        let contentText = '';
        let linkText = '';

        function isLink(text) {
            if (!text) return false;

            const value = text.trim().toLowerCase();

            return value.startsWith('http://') || value.startsWith('https://');
        }

        if (rawContent.includes(',')) {
            const parts = rawContent.split(',');

            const first = parts[0] ? parts[0].trim() : '';
            const second = parts.length > 1 ? parts.slice(1).join(',').trim() : '';

            if (isLink(first)) {
                linkText = first;
            } else {
                contentText = first;
            }

            if (isLink(second)) {
                linkText = second;
            }
        } else {
            if (isLink(rawContent)) {
                linkText = rawContent;
            } else {
                contentText = rawContent;
            }
        }

        contentInput.value = contentText;
        linkInput.value = linkText;
    }

    function initEditImageArea() {
        const imageList = document.getElementById('current-image-list');
        const fileInput = document.getElementById('fileInput');
        const addBtn = document.getElementById('image-add-btn');
        const countEl = document.getElementById('image-current-count');
        const previewImage = document.getElementById('main-preview-image');
        const previewText = document.getElementById('main-preview-text');

        const MAX_IMAGE_COUNT = 5;

        if (!imageList || !fileInput || !addBtn) return;

        // 처음 화면 들어왔을 때 기존 사진 기준으로 대표 이미지 세팅
        updateImageState();

        // + 이미지 추가 버튼 누르면 파일 선택창 열기
        addBtn.addEventListener('click', function () {
            fileInput.click();
        });

        // 파일 선택하면 새 이미지 미리보기 추가
        fileInput.addEventListener('change', function () {
            const selectedFiles = Array.from(fileInput.files);

            if (selectedFiles.length === 0) return;

            const currentCount = getImageBoxes().length;
            const availableCount = MAX_IMAGE_COUNT - currentCount;

            if (availableCount <= 0) {
                alert('이미지는 최대 5개까지 등록할 수 있습니다.');
                fileInput.value = '';
                return;
            }

            const filesToAdd = selectedFiles.slice(0, availableCount);

            if (selectedFiles.length > availableCount) {
                alert('이미지는 최대 5개까지 등록할 수 있어요. 가능한 개수만 추가됩니다.');
            }

            filesToAdd.forEach(function (file) {
                addNewImageBox(file);
            });

            updateImageState();
        });

        // 삭제 버튼 클릭 처리
        imageList.addEventListener('click', function (event) {
            const deleteBtn = event.target.closest('.current-image-delete-btn');

            if (!deleteBtn) return;

            const imageBox = deleteBtn.closest('.current-image');

            if (!imageBox) return;

            // 기존 이미지라면 삭제할 이미지 이름을 hidden으로 따로 보냄
            if (imageBox.dataset.imageType === 'existing') {
                const savedName = imageBox.dataset.savedName;

                if (savedName) {
                    addDeletedImageInput(savedName);
                }
            }

            imageBox.remove();
            updateImageState();
        });

        function getImageBoxes() {
            return Array.from(imageList.querySelectorAll('.current-image'));
        }

        function updateImageState() {
            const imageBoxes = getImageBoxes();

            imageBoxes.forEach(function (box) {
                box.classList.remove('is-main');
            });

            if (countEl) {
                countEl.textContent = imageBoxes.length + ' / ' + MAX_IMAGE_COUNT;
            }

           if (imageBoxes.length === 0) {
    const previewBox = document.getElementById('main-preview-box');

    if (previewBox) {
        previewBox.classList.remove('has-image');
    }

    if (previewImage) {
        previewImage.removeAttribute('src');
        previewImage.style.display = 'none';
    }

    if (previewText) {
        previewText.style.display = 'flex';
        previewText.textContent = '대표 이미지 미리보기';
    }

    return;
}

            const previewBox = document.getElementById('main-preview-box');

if (previewBox) {
    previewBox.classList.add('has-image');
}

const mainBox = imageBoxes[0];
mainBox.classList.add('is-main');

const mainImg = mainBox.querySelector('img');

if (mainImg && previewImage) {
    previewImage.src = mainImg.src;
    previewImage.style.display = 'block';
}

if (previewText) {
    previewText.style.display = 'none';
}
        }

        function addNewImageBox(file) {
            const imageUrl = URL.createObjectURL(file);

            const imageBox = document.createElement('div');
            imageBox.className = 'current-image';
            imageBox.dataset.imageType = 'new';

            const img = document.createElement('img');
            img.src = imageUrl;
            img.alt = '새로 추가한 이미지';

            const deleteBtn = document.createElement('button');
            deleteBtn.type = 'button';
            deleteBtn.className = 'current-image-delete-btn';
            deleteBtn.textContent = '×';

            imageBox.appendChild(img);
            imageBox.appendChild(deleteBtn);

            imageList.appendChild(imageBox);
        }

        function addDeletedImageInput(savedName) {
            const form = document.querySelector('form');

            if (!form) return;

            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'deletedImages';
            input.value = savedName;

            form.appendChild(input);
        }
    }
});