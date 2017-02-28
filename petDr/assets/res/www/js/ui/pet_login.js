var isJoin;
var st1,st2,st3;

M.onReady(function() {
	
	var $joinBtn = $("#joinBtn");
	var $loginBtn = $("#loginBtn");
	
	st1 = M.data.storage("pet_info1");
	st2 = M.data.storage("pet_info2");
	st3 = M.data.storage("pet_info3");
	
	//반려동물 등록 버튼 이벤트
	$joinBtn.click(function(){
		if(st3.name==null || st2.name==null || st1.name==null){
			M.page.html({
				url: 'pet_join.html',
				animation: 'MODAL_UP'
				});
		}else if(st3.name!=null && st2.name!=null && st1.name!=null){
			M.pop.alert({
				message:'반려견 등록은 3마리까지 가능합니다!',
				button:['확인']
			});
		}
	});
	
	
	
	//선택 이벤트
	$loginBtn.click(function(){
		var select = $("#name_select").val();
		
		if(select=="기본"){
			M.pop.alert({
				message:'반려견을 등록하거나 선택해주세요!',
				button:['확인']
			});
		}else{
			M.page.html({
				url: 'pet_board.html',
				param:{"select":select},
				animation: 'MODAL_UP'
				});
		}
		
	});
	
	setSelectView();

}).onHide(function() {

}).onResume(function() {
}).onPause(function() {
	
}).onRestore(function() {
	setSelectView();
}).onDestroy(function() {

}).onBack(function() {
	
}).onKey(function() {

})

//select box 내용 채우기
function setSelectView(){
	
	//강아지 등록된 데이터객체 가져오기
	var $pet_info1 = M.data.storage("pet_info1");
	var $pet_info2 = M.data.storage("pet_info2");
	var $pet_info3 = M.data.storage("pet_info3");
	
	var tempHtml = '';
	var $name_select = $("#name_select");
	
	var $name1 = $pet_info1.name;
	var $name2 = $pet_info2.name;
	var $name3 = $pet_info3.name;
	
	//select box 초기화
	$name_select.html("");
	
	tempHtml += '<option value="기본" selected="selected">반려견 선택 ▼</option>';
	
	if($name1!=null){
		tempHtml += '<option value="pet_info1">'+$name1+'</option>';
	}

	if($name2!=null){
		tempHtml += '<option value="pet_info2">'+$name2+'</option>';
	}

	if($name3!=null){
		tempHtml += '<option value="pet_info3">'+$name3+'</option>';
	}
	
	$name_select.html(tempHtml);
	
	
}

