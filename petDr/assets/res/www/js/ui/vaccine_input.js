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
	
	
	var $vaccineSaveBtn = $("#vaccineSaveBtn");
	var $backBtn = $("#backBtn");
	var $vaccine_select = $("#vaccine_select");
	
	
	$backBtn.click(function(){
		M.page.back({
			animation: "MODAL_DOWN"
		});
	});
	
	$("#vaccine_date").instance("Datepicker", {
		useCalendarIcon: true,
		iconTemplate: '<a href="#"><span></span></a>',
		dateFormat: "yy-mm-dd",
		disabled: false,
		calendarOptions: {}
	});
	
	/*저장
	 lastDate:등록날짜
	 nextDate:2주 후 날짜
	 num:투약횟수
	 */
	$vaccineSaveBtn.click(function(){
		var $is = $("#vaccine_date").instance("Datepicker").date();
		lastDate = $("#date_input").val();
		
		
		if($is==null){
			M.pop.alert({
				title: '예방접종 등록',
				message:'마지막 접종일을 입력하세요!',
				button:['확인']
			});
		}else
		{
			year = Number.parseInt(lastDate.substr(0,4));
			month = Number.parseInt(lastDate.substring(5,7));
			day = Number.parseInt(lastDate.substring(8,10));
			
		
			if($vaccine_select.val()=="혼합"){
				//혼합접종
				
				getTwoweek(year,month,day);
				
				M.pop.alert({
					title: '혼합접종 등록',
					message: '마지막 접종일을 '+lastDate+
						' 로 등록하시겠습니까?',
					button:['취소','확인'],
					callback:function(index){
						switch(index){
							case 1:
								var last_st = M.data.storage("hon"+info_n);
								
								if(last_st.num!=null){
									var num = last_st.num+1;
									var vaccine = {'last':lastDate,'next':nextDate,'num':num};
									M.data.storage("hon"+info_n,vaccine);
									
								}else if(last_st.num==null){
									var num = 1;
									var vaccine = {'last':lastDate,'next':nextDate,'num':num};
									M.data.storage("hon"+info_n,vaccine);
								}
								
								M.page.back({
									animation:"MODAL_DOWN"
								});
								
								M.tool.log(lastDate);
								break;
						}
					
				}
				});
			}else if($vaccine_select.val()=="코로나"){
				//코로나장염
				
				getTwoweek(year,month,day);
				
				M.pop.alert({
					title: '코로나장염 등록',
					message: '마지막 접종일을 '+lastDate+
						' 로 등록하시겠습니까?',
					button:['취소','확인'],
					callback:function(index){
						switch(index){
							case 1:
								var last_st = M.data.storage("ko"+info_n);
								
								if(last_st.num!=null){
									var num = last_st.num+1;
									var vaccine = {'last':lastDate,'next':nextDate,'num':num};
									M.data.storage("ko"+info_n,vaccine);
									
								}else if(last_st.num==null){
									var num = 1;
									var vaccine = {'last':lastDate,'next':nextDate,'num':num};
									M.data.storage("ko"+info_n,vaccine);
								}
								
								M.page.back({
									animation:"MODAL_DOWN"
								});
								
								break;
						}
					
				}
				});
			}else if($vaccine_select.val()=="전염성"){
				//전염성기관지염
				
				getTwoweek(year,month,day);
				
				M.pop.alert({
					title: '전염성 기관지염 등록',
					message: '마지막 접종일을 '+lastDate+
						' 로 등록하시겠습니까?',
					button:['취소','확인'],
					callback:function(index){
						switch(index){
							case 1:
								var last_st = M.data.storage("jun"+info_n);
								
								if(last_st.num!=null){
									var num = last_st.num+1;
									var vaccine = {'last':lastDate,'next':nextDate,'num':num};
									M.data.storage("jun"+info_n,vaccine);
									
								}else if(last_st.num==null){
									var num = 1;
									var vaccine = {'last':lastDate,'next':nextDate,'num':num};
									M.data.storage("jun"+info_n,vaccine);
								}
								
								M.page.back({
									animation:"MODAL_DOWN"
								});
								
								break;
						}
					
				}
				});
			}else if($vaccine_select.val()=="광견병"){
				//광견병
				
				getTwoweek(year,month,day);
				
				M.pop.alert({
					title: '광견병접종 등록',
					message: '마지막 접종일을 '+lastDate+
						' 로 등록하시겠습니까?',
					button:['취소','확인'],
					callback:function(index){
						switch(index){
							case 1:
								var last_st = M.data.storage("kwang"+info_n);
								
								if(last_st.num!=null){
									var num = last_st.num+1;
									var vaccine = {'last':lastDate,'next':nextDate,'num':num};
									M.data.storage("kwang"+info_n,vaccine);
									
								}else if(last_st.num==null){
									var num = 1;
									var vaccine = {'last':lastDate,'next':nextDate,'num':num};
									M.data.storage("kwang"+info_n,vaccine);
								}
								
								M.page.back({
									animation:"MODAL_DOWN"
								});
								
								break;
						}
					
				}
				});
			}else if($vaccine_select.val()=="개인"){
				//개인플루엔자
				getTwoweek(year,month,day);
				
				M.pop.alert({
					title: '개 인플루엔자 등록',
					message: '마지막 접종일을 '+lastDate+
						' 로 등록하시겠습니까?',
					button:['취소','확인'],
					callback:function(index){
						switch(index){
							case 1:
								var last_st = M.data.storage("ga"+info_n);
								
								if(last_st.num!=null){
									var num = last_st.num+1;
									var vaccine = {'last':lastDate,'next':nextDate,'num':num};
									M.data.storage("ga"+info_n,vaccine);
									
								}else if(last_st.num==null){
									var num = 1;
									var vaccine = {'last':lastDate,'next':nextDate,'num':num};
									M.data.storage("ga"+info_n,vaccine);
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

//2주후 계산 코드
function getTwoweek(year,month,day){
	
	if(month%2==0 && month!=2){
		day += 14;	
		
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
		
		day += 14;	
		
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
		
		day += 14;	
		
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
		