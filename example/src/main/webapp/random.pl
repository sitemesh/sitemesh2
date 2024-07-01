#!perl

$n = int (rand 1000) + 1;
$hour = (localtime)[2];

if ($hour > 3 && $hour < 12) {
	$t = "morning";
} 
elsif ($hour < 19) {
	$t = "afternoon";
}
elsif ($hour < 21) {
	$t = "evening";
}
else {
	$t = "night";
}

print <<"EOF";
Content-type: text/html

<html>
	<head>
		<title>Random number $n</title>
	</head>
	<body>
	
		The full power of Perl is being harnessed to generate the number of 
		<b>$n!</b>
		
		<p>
		
		Have a nice $t.
	
	</body>
</html>
EOF

