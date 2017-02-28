M.onReady(function() {
	var $backBtn = $("#backBtn");
	var $joinBtn = $("#joinBtn");
	var $searchPhoto = $("#searchPhoto");
	var $imgUrl;

	//뒤로가기
	$backBtn.click(function(){
		M.page.back({
			animation: "MODAL_DOWN"
		});
	});
	
	//사진등록
	$searchPhoto.click(function(){
		M.pop.alert('사진을 선택하세요',{
			title: '알림',
			buttons : ['갤러리','사진촬영','취소'],
			callback: function(index){
				chooseImg(index);
			}
		});
		
	});
	
	//애견등록
	$joinBtn.click(function(){
		var name = $("#inputName").val();
		var sp = $("#inputSp").val();
		var gender=$("#selGender").val();
		var age = $("#inputAge").val();
		var img = $("#profileImg").attr("src");
		
		var st1 = M.data.storage("pet_info1");
		var st2 = M.data.storage("pet_info2");
		
		if(checkJoinData(name,sp,age,img)){
			if(st1.name==null){
				var pet = {'name':name,'sp':sp,'gender':gender,'age':age,'imageUrl':img};
				M.data.storage("pet_info1",pet);
			}else if(st1.name!=null && st2.name==null){
				var pet = {'name':name,'sp':sp,'gender':gender,'age':age,'imageUrl':img};
				M.data.storage("pet_info2",pet);
			}else if(st1.name!=null && st2.name!=null){
				var pet = {'name':name,'sp':sp,'gender':gender,'age':age,'imageUrl':img};
				M.data.storage("pet_info3",pet);
			}
			
		}
		
		M.pop.alert({
			title: '알림',
			message:'등록을 성공했습니다',
			button:['확인'],
			callback:function(index){
				M.page.back({
					animation:"MODAL_DOWN"
				});
			}
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

//선택이미지 미리보기
function previewImg(path){
	var $img = $("#profileImg");
	$img.css("width",100);
	$img.removeClass("none");
	$img.attr("src",path);
}

//입력값 빈칸체크
function checkJoinData(name,sp,age,img){
	if(name.length < 1){
		M.pop.alert({
			message:'강아지 이름은 한자리 이상이여야 합니다.',
			button:['확인']
		});
		
		$("#inputName").focus();
		return false;
	}
	else if(sp < 1){
		M.pop.alert({
			message:'강아지 종류를 입력해주세요',
			button:['확인']
		});
		
		$("#inputSp").focus();
		return false;
	}
	else if(age<1){
		M.pop.alert({
			message:'강아지 나이를 입력해주세요',
			button:['확인']
		});
		
		$("#inputAge").focus();
		return false;
	}else if(img=="#"){
		M.pop.alert({
			message:'사진을 등록해주세요',
			button:['확인']
		});
		
		return false;
	}
	else{
		return true;
	}
	
}