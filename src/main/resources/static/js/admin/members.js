// 아이디 중복 확인 여부 플래그 변수
let isIdChecked = false;

// 회원 등록 모달 창 열기
function openRegisterModal() {
    document.getElementById('registerModal').style.display = "block";
}

// 회원 등록 모달 창 닫기
function closeRegisterModal() {
    document.getElementById('registerModal').style.display = "none";
    document.getElementById('registerForm').reset();
    document.getElementById('usernameError').textContent = "";
    document.getElementById('username').readOnly = false;
    document.querySelector('.check-btn').disabled = false;
    isIdChecked = false;
}

// 회원 상세정보 보기 모달 창 열기
function openDetailModal(username) {
    console.log(username);
    fetch(`/admin/members/${username}`,
        {method: 'GET'}
    )
    .then(response => {
      if(!response.ok) {
        throw new Error('네트워크에 문제가 발생했습니다.');
      }
      return response.json();
    })
    .then(member => {
        document.getElementById("detailUsername").value = member.username;
        document.getElementById("detailName").value = member.name;
        document.getElementById("detailNickname").value = member.nickname;
        document.getElementById("detailBirthday").value = member.birthday;
        document.getElementById("detailGender").value = member.gender;
        document.getElementById("detailSkinType").value = member.skinType;
        document.getElementById("detailPersonalColor").value = member.personalColor;
        document.getElementById("detailEmail").value = member.email;
        document.getElementById("detailPhone").value = member.phone;
        document.getElementById("detailPoint").value = member.point;
        document.getElementById("detailRole").value = member.role;
        document.getElementById("detailStatus").value = member.status;
        document.getElementById('detailModal').style.display = "block";
    })
    .catch(error => {
        console.error('Error:', error);
    });
}

// 일괄 변경 버튼의 팝오버 메뉴창 설정
function setUpPopoverContent(type) {
    const select = document.getElementById('popoverSelect');
    const input = document.getElementById('popoverInput');

    select.classList.add('hidden');
    input.classList.add('hidden');

    // 값을 직접 입력하는 포인트인지 확인
    // 포인트만 값을 직접 입력하고 나머지는 드롭다운으로 선택하기 때문에 나온 분기
    if(type == 'point') {
        input.classList.remove('hidden');
        input.value = '';
    } else {
        select.classList.remove('hidden');
        select.innerHTML = '';

        const options = { 
            'skinType' : [
                { val: '중성', text: '중성'},
                { val: '건성', text: '건성'},
                { val: '지성', text: '지성'},
                { val: '민감성', text: '민감성'},
                { val: '복합성', text: '복합성'},
                { val: '수부지', text: '수부지'}
            ],
            'personalColor' : [
                { val: '봄웜', text: '봄웜'},
                { val: '가을웜', text: '가을웜'},
                { val: '여름쿨', text: '여름쿨'},
                { val: '겨울쿨', text: '겨울쿨'}
            ],
            'role' : [
                { val: 'USER', text: '일반'},
                { val: 'ADMIN', text: '관리자'}
            ],
            'status' : [
                { val: 'normal', text: '정상'},
                { val: 'deleted', text: '탈퇴'}
            ]
        };

        options[type].forEach(opt => {
            const el = document.createElement('option');
            el.value = opt.val;
            el.innerText = opt.text;
            select.appendChild(el);
        });
    }
}

// 회원 상세정보 보기 모달 창 닫기
function closeDetailModal() {
    document.getElementById('detailModal').style.display = 'none';
}

document.addEventListener('DOMContentLoaded', () => {
    const tabContainer = document.querySelector('.tab-container');
    const tabBtns = tabContainer.children;

    const resetBtn = document.getElementById('resetBtn');
    const memberFilterForm = document.getElementById('memberFilterForm');

    const allCheck = document.getElementById('all-check');
    const memberChecks = document.querySelectorAll('.member-check');
    const registerBtn = document.getElementById('registerBtn');
    const closeBtn = document.querySelector('.close-btn');

    const usernameInput = document.getElementById('username');
    const registerForm = document.getElementById('registerForm');
    const checkDupBtn = document.querySelector('.check-btn');
    const usernameError = document.getElementById('usernameError');

    const popover = document.getElementById('batchPopover');
    const popoverSelect = document.getElementById('popoverSelect');
    const popoverInput = document.getElementById('popoverInput');
    const confirmBtn = document.getElementById('popoverConfirm');

    // 탭 메뉴를 통한 화면 전환 (회원 조회 <-> 회원 통계분석)
    const currentTab = tabContainer.getAttribute('data-current-tab');
    const sections = {
        'search' : document.getElementById('member-search'),
        'analysis' : document.getElementById('member-analysis')
    };
    Object.keys(sections).forEach(key => {
        sections[key].style.display = (key === currentTab) ? 'block' : 'none';
    });

    // 선택한 필터링 조건 초기화
    resetBtn.addEventListener('click', (e) => {
        memberFilterForm.querySelectorAll('input[type="checkbox"], input[type="radio"]')
            .forEach(item => {
                item.checked = false;
        })
        memberFilterForm.querySelectorAll('input[type="number"]').forEach(input => {
            input.value = '';
        })
    });

    // 다중 조건 필터링 (회원 통계)에서 포인트 범위를 하나만 적을 경우를 방지
    memberFilterForm.addEventListener('submit', function(e) {
        const min = document.querySelector('[name=minPoint]').value;
        const max = document.querySelector('[name=maxPoint]').value;

        if((min && !max) || (!min && max)) {
            e.preventDefault(); // 전송 취소
            alert("포인트는 최솟값과 최댓값을 모두 작성해야 합니다.");
            if(!max) document.querySelector('[name=maxPoint]').focus();
            else document.querySelector('[name=minPoint]').focus();
        }
    });

    // 선택된 체크박스 계산
    function countCheckedBox() {
        const checkedCount = document.querySelectorAll('.member-check:checked').length;
        const checkInfo = document.getElementById('checkInfo');
        const actionBtns = document.querySelectorAll('.btn-action');

        checkInfo.textContent = checkedCount == 0 ? '' : `* ${checkedCount}개 선택됨`;
        actionBtns.forEach(ac => {
            if(checkedCount > 0) {
                ac.classList.remove('hidden');
            } else {
                ac.classList.add('hidden');
            }
        });
    }
    // 이벤트 연결
    memberChecks.forEach(mc =>
        mc.addEventListener('change', countCheckedBox)
    )

    // 모든 항목 선택/해제
    allCheck.addEventListener('change', (e) => {
        document.querySelectorAll('.member-check').forEach(memberCheck => {
            memberCheck.checked = e.target.checked
        });
        countCheckedBox();
    })

    // 개별 선택 시에도 자동 반영
    memberChecks.forEach(memberCheck => {
        memberCheck.addEventListener('click', () => {
            const checkedCount = document.querySelectorAll(".member-check:checked").length;
            allCheck.checked = (checkedCount === memberChecks.length);
        })
    })

    // 아이디가 입력되지 않으면 중복 확인 버튼 비활성화
    if (usernameInput && checkDupBtn) {
        checkDupBtn.disabled = !usernameInput.value.trim();

        // 아이디 입력 여부에 따른 중복 확인 버튼 활성화 제어
        usernameInput.addEventListener('input', function() {
            // 입력된 값이 영어와 숫자 조합인지 확인
            const hasInvalidChar = /[^a-zA-Z0-9]/.test(usernameInput.value);

            if (hasInvalidChar) {
                // 영어와 숫자 조합이 아니라면 실시간으로 입력창에서 삭제
                usernameInput.value = usernameInput.value.replace(/[^a-zA-Z0-9]/g, '');
                // 잘못된 문자를 입력했다는 에러 메시지 출력
                if (usernameError) {
                    usernameError.style.color = '#FFA1C1';
                    usernameError.textContent = '아이디는 영문자와 숫자만 입력 가능합니다.';
                }
            } else {
                // 다시 아이디를 입력하기 시작하면 에러 메시지 초기화
                if (usernameError) {
                    usernameError.textContent = '';
                }
            }
            checkDupBtn.disabled = !usernameInput.value.trim();
        });

        // 중복 확인 버튼 클릭 이벤트
        checkDupBtn.addEventListener('click', function() {
            const username = usernameInput.value.trim();
            // 아이디가 입력된 값이 없다면 중복을 확인하지 않음
            // 브라우저 개발자 모드로 버튼을 활성화시키는 우회 조작을 방지
            if (!username) return;

            // 서버에게 GET방식으로 요청 (파라미터는 PathVariables 방식으로 넘김)
            fetch(`/chkid-dup/${username}`, {
                method: 'GET'
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('네트워크에 문제가 발생했습니다.');
                }
                return response.json(); // 서버에서 boolean 값을 리턴 (isDuplicate)
            })
            .then(isDuplicate => {
                if (isDuplicate) { // 중복된 아이디인 경우 메시지 출력
                    if (usernameError) {
                        usernameError.style.color = '#FFA1C1';
                        usernameError.textContent = '이미 사용중인 아이디입니다.';
                    }
                    isIdChecked = false; // 아이디 중복 확인되지 않음
                } else { // 새로운 아이디인 경우 아이디 입력창 및 버튼 비활성화 (readOnly)
                    checkDupBtn.disabled = true;
                    usernameInput.readOnly = true;
                    if (usernameError) {
                        usernameError.style.color = '#2DB400'
                        usernameError.textContent = '사용 가능한 아이디입니다.';
                    }
                    isIdChecked = true; // 아이디 중복 확인 완료
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        });
    }
    
    // 일괄 변경 버튼의 상세 옵션창(팝오버) 설정
    document.querySelectorAll('.btn-action').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const type = btn.dataset.updateType;
            // 팝 오버 출력
            popover.classList.remove('hidden');

            // 팝오버 폼 설정
            setUpPopoverContent(type);
            
            // 팝오버 위치 설정
            const rect = btn.getBoundingClientRect();
            popover.style.top = `${rect.bottom + window.scrollY + 5}px`;
            popover.style.left = `${rect.left + window.scrollX}px`;

            confirmBtn.dataset.currentType = type;
        });
    });

    // 일괄 변경 수행
    confirmBtn.addEventListener('click', () => {
        const type = confirmBtn.dataset.currentType;
        const value = (type === 'point') ? popoverInput.value : popoverSelect.value;
        const usernames = Array.from(document.querySelectorAll('.member-check:checked'))
            .map(cb => cb.closest('tr').cells[1].innerText);
        const payload = {
            usernames: usernames,
            modifyType: type,
            value: value
        };

        // fetch 요청
        fetch('/admin/members/modifies', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload) // 직렬화하여 문자열로 보냄
        })
        .then(response => {
            if(!response.ok) {
                throw new Error('서버 오류 발생');
            } else {
                location.reload(); // 현재 화면 새로고침
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("작업 중 오류 발생. 담당자에게 문의하세요.")
        });

        popover.classList.add('hidden'); // 작업 후 상세 옵션창(팝오버) 닫기
    })

    // 일괄 변경 상세 옵션창 바깥 클릭 시 상세 옵션창 닫기
    document.addEventListener('click', (e) => {
        const popover = document.getElementById('batchPopover');
        // 클릭한 요소가 팝오버 내부도 아니고 버튼도 아닐 때만 창 닫기
        if(!popover.contains(e.target) && !e.target.closest('.btn-action')) {
            popover.classList.add('hidden');
        }
    })

    // 등록하기 버튼 클릭 이벤트
    if (registerForm) {
        registerForm.addEventListener("submit", function(event) {
            // 아이디 중복 체크가 완료되지 않았다면
            // form에 입력된 모든 내용을 서버에 전송하지 않음
            if(!isIdChecked) {
                event.preventDefault(); // 전송 방지

                if(usernameError) {
                    usernameError.style.color = '#FFA1C1';
                    usernameError.textContent = '아이디 중복을 확인해주세요.';
                }

                if(usernameInput) {
                    usernameInput.focus();
                }
            }
        });
    }

});