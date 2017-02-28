(function(window, undefined) {

M.onBack(function(e) {

});

})(window);


//이미지선택 팝업창
function chooseImg(index) {
	switch(index) {
		case 0:
			M.media.picker({
		        mode: "SINGLE",
		        media: "PHOTO",
		        path: "/",
		        callback: function(status, result){ 
		        	imgResizing(result.path);
		        	
		        }
			});
			break;
		case 1:
			M.media.camera({
		        path: "/media",
		        mediaType: "PHOTO",
		        saveAlbum: true,
		        callback: function(status, result, option) {
		        	if (status == 'SUCCESS') {
	                	var photo_path = result.path;
	                    imgResizing(result.path);
	                }
		        }
			});
			break;
		case 2:
			break;
	}
	
}

//이미지 리사이징
function imgResizing(origin_path){
	M.media.optimize({
     source: origin_path,
     destination: '/download/' + Date.now() + '.jpg',
     overwrite: true,
     dimension: { width:200, height:200 },
     format: 'JPG',
     callback: function( result ) {
     		previewImg(result.path); 
     }
	});
}