const dropZone = document.getElementById('drop-zone');
const fileInput = document.getElementById('fileInput');
const previewContainer = document.getElementById('preview-container');
let filesArray = []; // 파일들을 저장하는 리스트

dropZone.onclick = () => fileInput.click();

// 드래그 해서 마우스를 올렸을 때
dropZone.addEventListener('dragover', (e) => {
    e.preventDefault(); // 파일 열기 동작 방지
    e.stopPropagation(); // 이벤트 전파 방지
    dropZone.style.borderColor = "var(--color-primary)";
});

// 드래그한 상태로 마우스를 치웠을 때
dropZone.addEventListener('dragleave', (e) => {
    e.preventDefault();
    e.stopPropagation();
    dropZone.style.borderColor = "var(--border-color)";
});

// 드래그한 아이템을 내려놓을 때
dropZone.addEventListener('drop', (e) => {
    e.preventDefault();
    e.stopPropagation();
    dropZone.style.borderColor = "var(--border-color)";
    
    const files = e.dataTransfer.files;
    handleFiles(files);
});

fileInput.onchange = (e) => {
    handleFiles(e.target.files);
};

// 파일 등록을 담당하는 핸들러
async function handleFiles(files) {
    if (filesArray.length + files.length > 20) {
        alert("최대 20개까지만 등록할 수 있습니다.");
        return;
    }

    Array.from(files).forEach(file => {
        if (file.size > 10 * 1024 * 1024) {
            alert(`${file.name} 파일이 10MB를 초과하여 등록할 수 없습니다.`);
            return;
        }
        filesArray.push(file);
    });

    await renderAllPreviews();
}

// 등록된 파일 삭제
function removeFile(index) {
    filesArray.splice(index, 1);
    renderAllPreviews();
}

// 파일 미리보기 이미지 렌더링
async function renderAllPreviews() {
    previewContainer.innerHTML = ''; // 화면 초기화
    
    // Promise를 사용하여 파일을 순서대로 읽음
    for (let i = 0; i < filesArray.length; i++) {
        const file = filesArray[i];
        const dataUrl = await readFileAsDataURL(file); // 순서대로 읽기 대기
        
        const div = document.createElement('div');
        div.className = 'preview-item';
        const fileSizeMB = (file.size / (1024 * 1024)).toFixed(2);
        
        div.innerHTML = `
            <button class="x-btn" type="button" onclick="removeFile(${i})">X</button>
            <img src="${dataUrl}" alt="미리보기">
            <div class="file-info-text">
                ${file.name}<br>(${fileSizeMB} MB)
            </div>
        `;
        previewContainer.appendChild(div);
    }
}

// 파일을 읽는 과정을 Promise로 감쌈 (순서 보장 핵심)
function readFileAsDataURL(file) {
    return new Promise((resolve) => {
        const reader = new FileReader();
        reader.onload = (e) => resolve(e.target.result);
        reader.readAsDataURL(file);
    });
}