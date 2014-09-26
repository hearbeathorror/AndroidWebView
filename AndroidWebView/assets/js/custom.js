/**
 * This just says hello when the page loads
 */
(function($){
	alertJs = function() {
		Android.alert('hello'); 
	};
})(jQuery);

/**
 * This function is only for trial
 */
function sayHello() {
	Android.alert('Onclick called');
}

/**
 * Drag and drop functions
 */
$(function() {
	$( ".draggable" ).draggable();
	$( ".droppable" ).droppable({
		drop: function( event, ui ) {
			if ($(this).hasClass("ui-droppable"))
			{
				$( this )
				.addClass( "ui-state-highlight" )
				.find( "p" )
				.html( "Dropped!" );
				alert("You've dropped in the right box!!");
			}
		}
	});
	$( ".droppable2" ).droppable({
		drop: function( event, ui ) {
			if ($(this).hasClass("ui-droppable2"))
			{
				$( this )
				.find( "p" )
				.html( "Dropped!" );
				Android.alert("You've dropped in the wrong box!!");
			}
		}
	});
	$(".draggable").draggable({
		revert : function(event, ui) {
			// $(this).data("ui-draggable")
			// Android.alert("alert : " + $(this).data("ui-draggable"));
			$(this).data("ui-draggable").originalPosition = {
				top : 0,
				left : 0
			};
			return !event;
		}
	});       
});

/**
 * loadImage takes a filepath as the source
 * And loads the image into the div that gets 
 * generated dynamically
 * @param src
 */
function loadImage(input) {
	console.log('loadImage ' + input);
	
	if(input !== undefined) {
		if (document.getElementById('banner_image').files && document.getElementById('banner_image').files[0]) {
			Android.alert('input: ' + document.getElementById('banner_image').files[0]);
			displayResizedImage(document.getElementById('banner_image').files[0]);
			//resizeFile(document.getElementById('banner_image').files[0]);
			//data:image/jpg;base64,xxxxxxxxxxxxxxxxxxxxxxxxxxxx
		}
	}else {
		console.log("input is blank");
	}
}

function displayResizedImageAsBytes(file) {
	Android.alert('file : ' + file);

	var stringToReplace = file;
	stringToReplace = stringToReplace.replace('data:base64,','data:image\/jpeg;base64,');

	$('#image').hide();
	console.log('reader result ' + file);

	var div = document.createElement("div");
	var img = new Image();

	div.setAttribute("id",'dynamicDiv' + $('#divImage').children().length + 1);
	div.setAttribute("class","draggable ui-widget-content ui-draggable");
	div.setAttribute("draggable","true");

	var divIDDynamic = '#dynamicDiv' + div.id;
	$(divIDDynamic).draggable();

	// Set the img src property using the data URL.
	img.src = stringToReplace;
	img.width = 100;
	img.height= 100;
	img.id = 'dynamicImage' + $('#divImage').children().length + 1; 

	div.appendChild(img);

	// Add the image to the page.
	$('#divImage').append(div);

	console.log('reader onloaded ' + img.src);

	$( ".draggable" ).draggable();

	$(".draggable").draggable({
		revert : function(event, ui) {
			$(this).data("ui-draggable").originalPosition = {
				top : 0,
				left : 0
			};
			return !event;
		}
	}); 
}

function displayResizedImage(file) {
	var reader = new FileReader();
	console.log('data from div element : ' + file.name);
	reader.readAsDataURL(file); 

	reader.onload = (function(file) {
		return function(e) {
			console.log('onload stage finished');
		};
	})(file);
	reader.onloadend = (function() {
		Android.alert('readerresult : ' + reader.result);

		var stringToReplace = reader.result;
		stringToReplace = stringToReplace.replace('data:base64,','data:image\/jpeg;base64,');

		$('#image').hide();
		console.log('reader result ' + reader.result);

		var div = document.createElement("div");
		var img = new Image();

		div.setAttribute("id",'dynamicDiv' + $('#divImage').children().length + 1);
		div.setAttribute("class","draggable ui-widget-content ui-draggable");
		div.setAttribute("draggable","true");

		var divIDDynamic = '#dynamicDiv' + div.id;
		$(divIDDynamic).draggable();

		// Set the img src property using the data URL.
		img.src = stringToReplace;
		img.width = 100;
		img.height= 100;
		img.id = 'dynamicImage' + $('#divImage').children().length + 1; 

		div.appendChild(img);

		// Add the image to the page.
		$('#divImage').append(div);

		console.log('reader onloaded ' + img.src);

		$( ".draggable" ).draggable();

		$(".draggable").draggable({
			revert : function(event, ui) {
				$(this).data("ui-draggable").originalPosition = {
					top : 0,
					left : 0
				};
				return !event;
			}
		}); 
	});
}

/**
 * for trial, show the image src on click
 * this image element is currently invisible
 * @param src
 */
function showSrc(src) {
	Android.alert('src : ' + src);
}

/**
 * support for touch and drag on mobile devices
 * and also desktop sites
 */
(function(b){
	b.support.touch="ontouchend" in document;
	if(!b.support.touch){
		return;
	}
	var c=b.ui.mouse.prototype,e=c._mouseInit,a;

	function d(g,h){
		if(g.originalEvent.touches.length>1){
			return;
		}
		g.preventDefault();
		var i=g.originalEvent.changedTouches[0],f=document.createEvent("MouseEvents");
		f.initMouseEvent(h,true,true,window,1,i.screenX,i.screenY,i.clientX,i.clientY,false,false,false,false,0,null);
		g.target.dispatchEvent(f);
	}

	c._touchStart=function(g){
		var f=this;
		if(a||!f._mouseCapture(g.originalEvent.changedTouches[0])){
			return;
		}
		a=true;
		f._touchMoved=false;
		d(g,"mouseover");
		d(g,"mousemove");
		d(g,"mousedown");
	};

	c._touchMove=function(f){
		if(!a){
			return;
		}
		this._touchMoved=true;
		d(f,"mousemove");
	};

	c._touchEnd=function(f){
		if(!a){
			return;
		}
		d(f,"mouseup");
		d(f,"mouseout");
		if(!this._touchMoved){
			d(f,"click");
		}
		a=false;
	};

	c._mouseInit=function(){
		var f=this;
		f.element.bind("touchstart",b.proxy(f,"_touchStart")).bind("touchmove",b.proxy(f,"_touchMove")).bind("touchend",b.proxy(f,"_touchEnd"));
		e.call(f);
	};
})(jQuery);

function resizeFile(inputFile) {
	var finalFile = null;
	var file = inputFile,
	fileType = file.type,
	reader = new FileReader();
	reader.readAsDataURL(file);
	reader.onloadend = function() {
		var image = new Image();
		image.src = reader.result;
		console.log("before image onload getting called!");

		var maxWidth = 240,
		maxHeight = 240;

		var canvas = document.createElement('canvas');
		canvas.width = maxWidth;
		canvas.height = maxHeight;

		var ctx = canvas.getContext("2d");
		ctx.drawImage(image, 0, 0, maxWidth, maxHeight);

		// The resized file ready for upload
		finalFile = canvas.toDataURL(fileType);
		displayResizedImageAsBytes(finalFile);

		/*image.onload = function() {
			console.log("image onload getting called!");
			var maxWidth = 240,
			maxHeight = 240,
			imageWidth = image.width,
			imageHeight = image.height;

			if (imageWidth > imageHeight) {
				if (imageWidth > maxWidth) {
					imageHeight *= maxWidth / imageWidth;
					imageWidth = maxWidth;
				}
			}
			else {
				if (imageHeight > maxHeight) {
					imageWidth *= maxHeight / imageHeight;
					imageHeight = maxHeight;
				}
			}

			var canvas = document.createElement('canvas');
			canvas.width = imageWidth;
			canvas.height = imageHeight;

			var ctx = canvas.getContext("2d");
			ctx.drawImage(this, 0, 0, imageWidth, imageHeight);

			// The resized file ready for upload
			finalFile = canvas.toDataURL(fileType);
			displayResizedImageAsBytes(finalFile);
		}*/
	}
	return finalFile;
}
