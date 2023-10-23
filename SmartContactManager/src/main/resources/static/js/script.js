console.log("this is script page")
const toggleSidebar = () => {
	if($(".sidebar").is(":visible"))
	{
	// true	
	// band karna hai visible ko
	$(".sidebar").css("display", "none")
	$(".content").css("margin-left", "0%")
	}
	else 
	{
	//false	
	// show karna ahi 
	$(".sidebar").css("display", "block")
	$(".content").css("margin-left", "20%")
	}	
};
// Search Bar ka js code ja query laka jaga backend ma aur rest search controller ko call kraga..
const search= () =>{
//	console.log("Searching.....");
    let  query = $("#search-input").val();
    
    
    if(query=="")
    {
		$(".search-result").hide();
	}
	else
	{
//    Searching....	
      console.log(query);	
 //    Sending request to server...
      let url = `http://localhost:8080/search/${query}`;

      
      fetch(url)
      .then((response) => {
		  return response.json();
	  })
	  .then((data) => {
	// data
	//console.log(data);	
	let text=`<div class='text-group'>` ;
	
	data.forEach((contact) => {
		text+=`<a href='/user/contact/${contact.id}' class='list-group-item list-group-item-action'> ${contact.name} </a>`
	});
	
	text += `</div>`;
	
	$(".search-result").html(text);
	$(".search-result").show();
	  });     
      
      
	}
    
};