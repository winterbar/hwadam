document.addEventListener("DOMContentLoaded", function () {

  const filterBar = document.getElementById("feed-filter-bar");
  const filterSummary = document.getElementById("feed-filter-summary");
  const filterToggleIcon = document.getElementById("feed-filter-toggle-icon");
  const filterInner = document.getElementById("feed-filter-inner");
  const feedEmpty = document.getElementById("feed-empty");


  // 해시태그 중복 제거 기능
  function removeDuplicateTagButtons() {
    const tagButtons = document.querySelectorAll(".feed-tag-btn");
    const seenTags = new Set();

    tagButtons.forEach(function (button) {
      const tag = button.dataset.tag
        ? button.dataset.tag.replace("#", "").trim().toLowerCase()
        : "";

      if (!tag) return;

      if (seenTags.has(tag)) {
        button.remove();
      } else {
        seenTags.add(tag);
      }
    });
  }


  // 상단 해시태그 필터 영역 열기 / 닫기

function toggleFeedFilter() {
if (filterSummary) {
  filterSummary.addEventListener(
    "click",
    function (event) {
      event.preventDefault();
      event.stopImmediatePropagation();

      const isClosed = filterBar.classList.contains("is-closed");

      if (isClosed) {
        filterBar.classList.remove("is-closed");

        filterInner.style.removeProperty("display");
        filterInner.style.setProperty("display", "flex", "important");

        filterToggleIcon.innerText = "접기 －";
      } else {
        filterBar.classList.add("is-closed");

        filterInner.style.removeProperty("display");
        filterInner.style.setProperty("display", "none", "important");

        filterToggleIcon.innerText = "펼치기 ＋";
      }
    },
    true
  );

  filterSummary.addEventListener(
    "keydown",
    function (event) {
      if (event.key !== "Enter" && event.key !== " ") return;

      event.preventDefault();
      event.stopImmediatePropagation();
      filterSummary.click();
    },
    true
  );
}
}


  // 선택한 해시태그에 해당하는 피드만 보여주기
  function applyFilter(selectedTag) {
    const feedCards = document.querySelectorAll(".feed-card");
    let visibleCount = 0;

    feedCards.forEach(function (card) {
      const cardTags = card.dataset.tags || "";

      if (selectedTag === "all" || cardTags.includes(selectedTag)) {
        card.style.display = "block";
        visibleCount++;
      } else {
        card.style.display = "none";
      }
    });

    if (feedEmpty) {
      feedEmpty.style.display = visibleCount === 0 ? "block" : "none";
    }
  }


  // 피드 본문 아래 해시태그 버튼 클릭
  function initHashtagButtons() {
    document.querySelectorAll(".feed-hashtag-btn").forEach(function (button) {
      button.addEventListener("click", function () {
        const selectedTag = button.dataset.tag;

        document.querySelectorAll(".feed-tag-btn").forEach(function (tagBtn) {
          tagBtn.classList.remove("active");

          if (tagBtn.dataset.tag === selectedTag) {
            tagBtn.classList.add("active");
          }
        });

        applyFilter(selectedTag);

        if (filterInner) {
          filterInner.style.display = "flex";
        }

        if (filterBar) {
          filterBar.classList.remove("is-closed");
        }

        if (filterToggleIcon) {
          filterToggleIcon.innerText = "접기 －";
        }
      });
    });
  }



  if (filterInner) {
    filterInner.addEventListener("click", function (event) {   
      if (!event.target.classList.contains("feed-tag-btn")) return;

      document.querySelectorAll(".feed-tag-btn").forEach(function (button) {
        button.classList.remove("active");
      });

      event.target.classList.add("active");
      applyFilter(event.target.dataset.tag);
    });
  }

  //실시간 가장 많이 작성한 5개 해시태그 보여주기 
  function renderToTags(){
    const trendList = document.getElementById("feed-trend-list");
    
    if(!trendList) return;
    //해시태그 사용 개수 저장할 객체 생성
    const tagCountMap={};
    //html에서 값을 가져와서 하나씩 반복
    document.querySelectorAll(".feed-card").forEach(function (card){
      const rawTags = card.dataset.tags ||"";
      // 태그가 존재하지 않는다면 끝
      if(!rawTags.trim()) return;
      // ,기준으로 태그 나누기
      rawTags.split(",")
      //빈 문자열 태그 제외
        .filter(function (tag){
          return tag !=="";
        })
        //태그 개수 세기
        .forEach(function (tag){
          tagCountMap[tag] = (tagCountMap[tag]||0)+1;
        });
    });
    //태그 개수 객체로 바꾸고 많이 사용된 순서로 정렬
    const topTags = Object.keys(tagCountMap)
      .map(function (tag){
        return{
          tag:tag,
          count:tagCountMap[tag],
        };
      })
      .sort(function (a,b){
        return b.count - a.count;
      })
      //5개
      .slice(0,5);
    // 기존 화면 비움
    trendList.innerHTML="";
    //태그 하나도 없을 경우 
    if(topTags.length===0){
      const emptyLi = document.createElement("li");
      emptyLi.className = "feed-trend-empty";
      emptyLi.textContent = "작성된 태그가 없습니다."
      trendList.appendChild(emptyLi);
      return;
    }
    topTags.forEach(function (item,index){
      const li = document.createElement("li");
      const left = document.createElement("div");
      left.className ="feed-trend-left";
      const rank = document.createElement("span");
      rank.className = "feed-trend-rank";
      // 1부터 시작하도록 설정
      rank.textContent = index +1;
      const tag = document.createElement("button");
      tag.type="button";
      tag.className="feed-trend-tag";
      tag.dataset.tag = item.tag;
      tag.textContent ="#"+item.tag;

      const count = document.createElement("span");
      count.className = "feed-trend-count";
      count.textContent = item.count+"개"

      left.appendChild(rank);
      left.appendChild(tag);
      li.appendChild(left);
      li.appendChild(count);
      trendList.appendChild(li);

    });

    trendList.addEventListener("click",function (event){
      const trendTag = event.target.closest(".feed-trend-tag");
      if(!trendTag) return;
      const selectedTag = trendTag.dataset.tag;
      document.querySelectorAll(".feed-tag-btn").forEach(function (button){
        button.classList.remove("active");

        if(button.dataset.tag===selectedTag){
          button.classList.add("active");
        }
      });
      applyFilter(selectedTag);
    });
  }
  
  // 실행
  removeDuplicateTagButtons();
  initHashtagButtons();
  renderToTags();
});