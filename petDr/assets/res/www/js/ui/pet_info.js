var select;
var info_n;

M.onReady(function() {
	select = M.data.param('select');
	
	if(select=="pet_info1")
		info_n=1;
	else if(select=="pet_info2")
		info_n=2;
	else if(select=="pet_info3")
		info_n=3;
	
	var $pet_photo = $("#pet_photo");
	var $gender_photo = $("#gender_photo");
	var $deleteBtn = $("#deleteBtn");
	var $changeBtn= $("#changeBtn");
	var $closeBtn = $("#closeBtn");
	
	
	
	//반려동물 정보 띄우기
	var $pet_info = M.data.storage(select);

	$pet_photo.attr("src",$pet_info.imageUrl);
	
	$("#pet_name").attr("value",$pet_info.name);
	
	 
	if($pet_info.gender=="여"){
		$gender_photo.attr("src","../img/female.png");
		$("#pet_gender").attr("value","여");
	}else if($pet_info.gender=="남"){
		$gender_photo.attr("src","../img/male.png");
		$("#pet_gender").attr("value","남");
	}
		
	$("#pet_age").attr("value",$pet_info.age);
	$("#pet_sp").attr("value",$pet_info.sp);
	
	
	//데이터 삭제
	$deleteBtn.click(function(){
		M.pop.alert({
			title: '삭제알림',
			message: '반려견 데이타를 삭제하시겠습니까?',
			button:['취소','확인'],
			callback:function(index){
				switch(index){
					case 1:
						removeData();

						
						M.page.html({
							url:'index.html',
							animation:'MODAL_UP'
						});
						
						break;
				}
			
		}
		});

	});
	
	//데이터 수정
	$changeBtn.click(function(){
		M.pop.alert({
			title: '수정알림',
			message: '반려견 데이타를 수정하시겠습니까?',
			button:['취소','확인'],
			callback:function(index){
				switch(index){
					case 1:
						changeData();
						
						M.page.back({
							animation:'MODAL_UP'
						});
						
						break;
				}
			
		}
		});
	});
	
	$closeBtn.click(function(){
		M.page.back({
			animation: "MODAL_DOWN"
		});
	});
}).onHide(function() {

}).onResume(function() {

}).onPause(function() {
	
}).onRestore(function() {
	
}).onDestroy(function() {

}).onBack(function() {
	M.page.back({
		animation: "MODAL_DOWN"
	});
}).onKey(function() {

})

//삭제코드
function removeData(){
	M.data.removeStorage(select);
	M.data.removeStorage("sim"+info_n);
	M.data.removeStorage("ga"+info_n);
	M.data.removeStorage("hon"+info_n);
	M.data.removeStorage("gi"+info_n);
	M.data.removeStorage("jun"+info_n);
	M.data.removeStorage("ko"+info_n);
	M.data.removeStorage("kwang"+info_n);
	
}

//수정코드
function changeData(){
	var $input_name = $("#pet_name").val();
	var $input_age = $("#pet_age").val();
	var $input_gender = $("#pet_gender").val();
	var $input_sp = $("#pet_sp").val();
	
	var $pet_info = M.data.storage(select);
	
	imgeUrl = $pet_info.imageUrl;
	
	var pet = {'name':$input_name,'sp':$input_sp,'gender':$input_gender,'age':$input_age,'imageUrl':imgeUrl};
	M.data.storage(select,pet);
}