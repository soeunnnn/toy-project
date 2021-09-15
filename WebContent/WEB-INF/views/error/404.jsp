<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style type="text/css">
html {
  height: 100%;
}

body {
  height: 100%;
  background: url("https://wallpapercave.com/wp/6SLzBEY.jpg") no-repeat left top;
  background-size: cover;
  overflow: hidden;
  display: flex;
  flex-flow: column wrap;
  justify-content: center;
  align-items: center;
}

.text h1 {
  color: #011718;
  margin-top: -200px;
  font-size: 15em;
  text-align: center;
  text-shadow: -5px 5px 0px rgba(0, 0, 0, 0.7), -10px 10px 0px rgba(0, 0, 0, 0.4), -15px 15px 0px rgba(0, 0, 0, 0.2);
  font-family: monospace;
  font-weight: bold;
}

.text h2 {
  color: black;
  font-size: 5em;
  text-shadow: -5px 5px 0px rgba(0, 0, 0, 0.7);
  text-align: center;
  margin-top: -150px;
  font-family: monospace;
  font-weight: bold;
}

.text h3 {
  color: white;
  margin-left: 30px;
  font-size: 2em;
  text-shadow: -5px 5px 0px rgba(0, 0, 0, 0.7);
  margin-top: -40px;
  font-family: monospace;
  font-weight: bold;
}

.torch {
  margin: -150px 0 0 -150px;
  width: 200px;
  height: 200px;
  box-shadow: 0 0 0 9999em #000000f7;
  opacity: 1;
  border-radius: 50%;
  position: fixed;
  background: rgba(0, 0, 0, 0.3);
}
.torch:after {
  content: "";
  display: block;
  border-radius: 50%;
  width: 100%;
  height: 100%;
  top: 0px;
  left: 0px;
  box-shadow: inset 0 0 40px 2px #000, 0 0 20px 4px rgba(13, 13, 10, 0.2);
}
</style>
</head>
<body>
<div class="text">
  <h1>404</h1>
  <h2>Uh, Ohh</h2>
  <h3>Sorry we cant find what you are looking for 'cuz its so dark in here</h3>
</div>
<div class="torch"></div>

<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script type="text/javascript">
$(document).mousemove(function (event) {
     $('.torch').css({
       'top': event.pageY,
       'left': event.pageX
     });
   });

</script>


</body>
</html>