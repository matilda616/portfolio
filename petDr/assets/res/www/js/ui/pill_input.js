var year,month,day,nextDate;
var $lastDate,$nextYear,$nextMon, $nextDay;
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
	
	
	var $pillSaveBtn = $("#pillSaveBtn");
	var $pill_select = $("#pill_select");
	var $backBtn = $("#backBtn");
	
	$backBtn.click(function(){
		M.page.back({
			animation: "MODAL_DOWN"
		});
	});
	
	
	
	$("#pill_date").instance("Datepicker", {
		useCalendarIcon: true,
		iconTemplate: '<a href="#"><span></span></a>',
		dateFormat: "yy-mm-dd",
		disabled: false,
		calendarOptions: {}
	});
	
	/*저장
	 lastDate:등록날짜
	 nextDate:한달 후 날짜
	 num:투약횟수
	 */
	$pillSaveBtn.click(function(){
		var $is = $("#pill_date").instance("Datepicker").date();
		lastDate = $("#date_input").val();
		
		if($is==null){
			M.pop.alert({
				title: '약 투여 등록',
				message:'마지막 투여일을 입력하세요!',
				button:['확인']
			});
		}else{
			year = Number.parseInt(lastDate.substr(0,4));
			month = Number.parseInt(lastDate.substring(5,7));
			day = Number.parseInt(lastDate.substring(8,10));
			
			if($pill_select.val()=="심장"){
				
				getOneMon(year,month,day);
				
				M.pop.alert({
					title: '심장사상충  등록',
					message: '마지막 투여일을 '+lastDate+
						' 로 등록하시겠습니까?',
					button:['취소','확인'],
					callback:function(index){
						switch(index){
							case 1:
								var last_st = M.data.storage("sim"+info_n);
								
								if(last_st.num!=null){
									var num = last_st.num+1;
									var vaccine = {'last':lastDate,'next':nextDate};
									M.data.storage("sim"+info_n,vaccine);
									
								}else if(last_st.num==null){
									var num = 1;
									var vaccine = {'last':lastDate,'next':nextDate};
									M.data.storage("sim"+info_n,vaccine);
								}
								
								M.page.back({
									animation:"MODAL_DOWN"
								});
								
								break;
						}
					
				}
				});
			}else if($pill_select.val()=="기생충"){
				
				getOneMon(year,month,day);
				
				M.pop.alert({
					title: '내,외부 기생충약  등록',
					message: '마지막 투여일을 '+lastDate+
						' 로 등록하시겠습니까?',
					button:['취소','확인'],
					callback:function(index){
						switch(index){
							case 1:
								var last_st = M.data.storage("gi"+info_n);
								
								if(last_st.num!=null){
									var num = last_st.num+1;
									var vaccine = {'last':lastDate,'next':nextDate};
									M.data.storage("gi"+info_n,vaccine);
									
								}else if(last_st.num==null){
									var num = 1;
									var vaccine = {'last':lastDate,'next':nextDate};
									M.data.storage("gi"+info_n,vaccine);
								}
								
								M.page.back({
									animation:"MODAL_DOWN"
								});
								
								break;
						}
					
				}
				});
			}
		
		}
		
		
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

//한달 후 계산 코드
function getOneMon(year,month,day){
	if(month%2==0 && month!=2){
		day += 30;	
		
		if(day>30){
			$nextDay=day%30;
			month+=1;
			if(month==13){
				$nextMon = month%12;
				$nextYear = year+1;
			}else{
				$nextMon = month;
				$nextYear = year;
		}
		
		}else if(day<30){
			$nextDay=day;
			$nextMon=month;
			$nextYear=year;
		}
		
		
		
	}else if(month%2==1){
		//홀수달
		
		day += 30;	
		
		if(day>31){
			$nextDay=day%31;
			month+=1;
			if(month==13){
				$nextMon = month%12;
				$nextYear = year+1;
			}else{
				$nextMon = month;
				$nextYear = year;
			}
				
		}else if(day<31){
			$nextDay=day;
			$nextMon=month;
			$nextYear=year;
		}
	}else if(month==2){
		//2월달
		
		day += 30;	
		
		if(day>28){
			$nextDay=day%28;
			month+=1;
			if($month==13){
				$nextMon = month%12;
				$nextYear = year+1;
			}else{
				$nextMon = month;
				$nextYear = year;
			}
		
		}else if(day<28){
			$nextDay=day;
			$nextMon=month;
			$nextYear=year;
		}
	}
	
	nextDate = $nextYear+"-"+$nextMon+"-"+$nextDay;
}
