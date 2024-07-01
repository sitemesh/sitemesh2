#!perl

sub mandelbrot {
	$Cols=79; $Lines=30;
	$MaxIter=16;
	$MinRe=-2.0; $MaxRe=1.0;
	$MinIm=-1.0; $MaxIm=1.0;
	@chars=(' ','.',',','-',':','/','=','H','O','A','M','%','&','$','#','@','_');
	my $result = "\n";
	for($Im=$MinIm;$Im<=$MaxIm;$Im+=($MaxIm-$MinIm)/$Lines)
	{ for($Re=$MinRe;$Re<=$MaxRe;$Re+=($MaxRe-$MinRe)/$Cols)
	  { $zr=$Re; $zi=$Im;
	    for($n=0;$n<$MaxIter;$n++)
	    { $a=$zr*$zr; $b=$zi*$zi;
	      if($a+$b>4.0) { last; }
	      $zi=2*$zr*$zi+$Im; $zr=$a-$b+$Re;
	    }
	    $result .= $chars[$n];
	  }
	  $result .= "\n";
	}
	$result;
}

my $m = mandelbrot();

print "Content-type: text/html\n\n";

print <<"EOF";
<html>
	<head>
		<title>Perl Mandelbrot</title>
	</head>
	<body>
		<p>I am a Perl CGI</p>
		
		<pre>
			$m
		</pre>
	
	</body>
</html>
EOF

