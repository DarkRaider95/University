integer x;
begin
	x:= 0;
	while x<100 do
		begin
			x:= x+1;
			print(x);
			if x>50 then print(true)
			else print(false)
		end
end