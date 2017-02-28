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
	var $pillBtn = $("#pillBtn");
	var $vaccineBtn=$("#vaccineBtn");
	
	//반려동물 정보 띄우기
	var $pet_info = M.data.storage(select);
	
	$pet_photo.attr("src",$pet_info.imageUrl);
	$("#pet_name").text($pet_info.name);
	
	if($pet_info.gender=="여")
		$gender_photo.attr("src","../img/female.png");
	else if($pet_info.gender=="남")
		$gender_photo.attr("src","../img/male.png");

	//반려동물 상세정보 창으로 이동
	$("#inform_photo").click(function(){
		M.page.html({
			url:'pet_info.html',
			param:{"select":select},
			animation:'MODAL_UP'
		});
	});
	
	
	//투약일 등록하기
	$pillBtn.click(function(){
		M.page.html({
			url:'pill_input.html',
			param:{"select":select},
			animation:'MODAL_UP'
		});
	});

	//접종일 등록하기
	$vaccineBtn.click(function(){
		M.page.html({
			url:'vaccine_input.html',
			param:{"select":select},
			animation:'MODAL_UP'
		});
	});
	
	setVacBoardList();
	setPillBoardList();
	
}).onHide(function() {

}).onResume(function() {
	
}).onPause(function() {
	
}).onRestore(function() {
	setVacBoardList();
	setPillBoardList();
}).onDestroy(function() {

}).onBack(function() {
	M.page.back({
		animation: "MODAL_DOWN"
	});
}).onKey(function() {

})


//예방접종 board 리스트 초기화
function setVacBoardList(){
	var tempHtml ='';
	
	//table tr
	var $hon = $("#hon");
	var $ko = $("#ko");
	var $jun = $("#jun");
	var $kwang = $("#kwang");
	var $ga = $("#ga");
	
	//storage data
	var $hon_data = M.data.storage("hon"+info_n);
	var $ko_data = M.data.storage("ko"+info_n);
	var $jun_data = M.data.storage("jun"+info_n);
	var $kwang_data = M.data.storage("kwang"+info_n);
	var $ga_data = M.data.storage("ga"+info_n);
	
	
	//혼합접종
	
	$hon.html("");
	
	if($hon_data.last==null){
		tempHtml+='<td><p align="center">혼합백신</p></td>';
		tempHtml+='<td colspan=3><p align="center">마지막 접종일을 등록해주세요</p></td>';
	}else if($hon_data.last!=null){
		tempHtml+='<td><p align="center">혼합백신</p></td>';
		tempHtml+='<td><p align="center">'+$hon_data.last+'</p></td>';
		tempHtml+='<td><p align="center">'+$hon_data.next+'</p></td>';
		tempHtml+='<td><p align="center">5회/'+$hon_data.num+'회</p></td>';
	}
	
	$hon.html(tempHtml);
	tempHtml ='';
	
	//코로나 접종
	$ko.html("");
	
	if($ko_data.last==null){
		tempHtml+='<td><p align="center">코로나장염</p></td>';
		tempHtml+='<td colspan=3><p align="center">마지막 접종일을 등록해주세요</p></td>';
	}else if($ko_data.last!=null){
		tempHtml+='<td><p align="center">코로나장염</p></td>';
		tempHtml+='<td><p align="center">'+$ko_data.last+'</p></td>';
		tempHtml+='<td><p align="center">'+$ko_data.next+'</p></td>';
		tempHtml+='<td><p align="center">3회/'+$ko_data.num+'회</p></td>';
	}
	
	$ko.html(tempHtml);
	tempHtml ='';
	
	//전염성 기관지염
	$jun.html("");
	
	if($jun_data.last==null){
		tempHtml+='<td><p align="center">전염성기관지염</p></td>';
		tempHtml+='<td colspan=3><p align="center">마지막 접종일을 등록해주세요</p></td>';
	}else if($jun_data.last!=null){
		tempHtml+='<td><p align="center">전염성기관지염</p></td>';
		tempHtml+='<td><p align="center">'+$jun_data.last+'</p></td>';
		tempHtml+='<td><p align="center">'+$jun_data.next+'</p></td>';
		tempHtml+='<td><p align="center">3회/'+$jun_data.num+'회</p></td>';
	}
	
	$jun.html(tempHtml);
	tempHtml ='';
	
	//광견병
	$kwang.html("");
	
	if($kwang_data.last==null){
		tempHtml+='<td><p align="center">광견병접종</p></td>';
		tempHtml+='<td colspan=3><p align="center">마지막 접종일을 등록해주세요</p></td>';
	}else if($kwang_data.last!=null){
		tempHtml+='<td><p align="center">광견병접종</p></td>';
		tempHtml+='<td><p align="center">'+$kwang_data.last+'</p></td>';
		tempHtml+='<td><p align="center">'+$kwang_data.next+'</p></td>';
		tempHtml+='<td><p align="center">1회/'+$kwang_data.num+'회</p></td>';
	}
	
	$kwang.html(tempHtml);
	tempHtml ='';
	
	//개 인플루엔자
	$ga.html("");
	
	if($ga_data.last==null){
		tempHtml+='<td><p align="center">개 인플루엔자</p></td>';
		tempHtml+='<td colspan=3><p align="center">마지막 접종일을 등록해주세요</p></td>';
	}else if($ga_data.last!=null){
		tempHtml+='<td><p align="center">개 인플루엔자</p></td>';
		tempHtml+='<td><p align="center">'+$ga_data.last+'</p></td>';
		tempHtml+='<td><p align="center">'+$ga_data.next+'</p></td>';
		tempHtml+='<td><p align="center">2회/'+$ga_data.num+'회</p></td>';
	}
	
	$ga.html(tempHtml);
	tempHtml ='';
}

//약 투여 board 내용 초기화
function setPillBoardList(){
	var tempHtml ='';
	
	//table tr
	var $sim = $("#sim");
	var $gi = $("#gi");
	
	
	//storage data
	var $sim_data = M.data.storage("sim"+info_n);
	var $gi_data = M.data.storage("gi"+info_n);
	
	//심장사상충 접종
	$sim.html("");
	
	if($sim_data.last==null){
		tempHtml+='<td><p align="center">심장사상충</p></td>';
		tempHtml+='<td colspan=3><p align="center">마지막 투여일을 등록해주세요</p></td>';
	}else if($sim_data.last!=null){
		tempHtml+='<td><p align="center">심장사상충</p></td>';
		tempHtml+='<td><p align="center">'+$sim_data.last+'</p></td>';
		tempHtml+='<td><p align="center">'+$sim_data.next+'</p></td>';
		tempHtml+='<td><p align="center">월 1회</p></td>';
	}
	
	$sim.html(tempHtml);
	tempHtml ='';
	
	//내,외부 기생충
	$gi.html("");
	
	if($gi_data.last==null){
		tempHtml+='<td><p align="center">내,외부 기생충</p></td>';
		tempHtml+='<td colspan=3><p align="center">마지막 투여일을 등록해주세요</p></td>';
	}else if($gi_data.last!=null){
		tempHtml+='<td><p align="center">내,외부 기생충</p></td>';
		tempHtml+='<td><p align="center">'+$gi_data.last+'</p></td>';
		tempHtml+='<td><p align="center">'+$gi_data.next+'</p></td>';
		tempHtml+='<td><p align="center">월 1회</p></td>';
	}
	
	$gi.html(tempHtml);
	tempHtml ='';
}