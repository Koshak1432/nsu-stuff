check_one(unsigned long long):
		push    rbp 					//save rbp
		mov     rbp, rsp 				//stack top to rbp
		mov     QWORD PTR [rbp-8], rdi	//move num to rbp-8
		mov     rax, QWORD PTR [rbp-8] 	//move num to rax
		and     eax, 1					// num in eax, check %2
		test    rax, rax
		jne     .L2						//if not even
		fld1 							//load to fpu +1.0
		jmp     .L4
.L2:
		fld1		//load to fpu +1.0
		fchs		//change a sign
.L4:
		pop     rbp	//restore rbp
		ret 		//pop return address from stack and jump there
count_pi(unsigned long long): //, -16 -- res, -24 -- i, -40 -- num, -48 -- the statement in loop after /
		push    rbp	//save rbp
		mov     rbp, rsp // stack top to rbp
		sub     rsp, 48 //save memory for local variables
		mov     QWORD PTR [rbp-40], rdi //8 byte for num
		fldz	//push 0 to fpu (st(0))
		fstp    TBYTE PTR [rbp-16] //copy the value from st(0) to res
		mov     QWORD PTR [rbp-24], 0 //i = 0 (-8 byte from res)
		jmp     .L6
.L8:
		mov     rax, QWORD PTR [rbp-24] //move i to rax
		mov     rdi, rax
		call    check_one(unsigned long long)
		fld     TBYTE PTR .LC4[rip] //load 4.0 on stack
		fmulp   st(1), st //multiply with expulsion 4.0 and the result of check_one
		mov     rax, QWORD PTR [rbp-24] //move i to rax
		add     rax, rax // add instead of mul
		add     rax, 1
		mov     QWORD PTR [rbp-48], rax //move the result to the rbp -48
		fild    QWORD PTR [rbp-48] //load signed int
		test    rax, rax
		jns     .L7 //if >= 0
		fld     TBYTE PTR .LC5[rip] //push 2^64
		faddp   st(1), st // правит знак, если вдруг добавил слишком большое число, которое стало в итоге отрицательным
.L7:
		fdivp   st(1), st //4.0 * check_one(i)/(2 * i + 1)
		fld     TBYTE PTR [rbp-16] //load res
		faddp   st(1), st // add previous  and new res
		fstp    TBYTE PTR [rbp-16] //res = prev + new
		add     QWORD PTR [rbp-24], 1 // ++i
.L6:
		mov     rax, QWORD PTR [rbp-24] //move i to rax
		cmp     rax, QWORD PTR [rbp-40] //cmp i and num
		jb      .L8 //if i < num
		fld     TBYTE PTR [rbp-16] //load res to fpu
		leave // restore stack and frame condition that was before the call
		ret  //back to the return address
.LC6:
		.string "result : %.20Lf\n"
main:
		push    rbp
		mov     rbp, rsp
		sub     rsp, 16 //save memory for local vars
		movabs  rax, 9000000000 //N to rax
		mov     QWORD PTR [rbp-8], rax // rax to rbp - 8
		mov     rax, QWORD PTR [rbp-8]
		mov     rdi, rax
		call    count_pi(unsigned long long)
		lea     rsp, [rsp-16] //load count_pi address to rsp
		fstp    TBYTE PTR [rsp] //put the result of count_pi to rsp
		mov     edi, OFFSET FLAT:.LC6
		mov     eax, 0 //put in eax how many vectors (xmm) used
		call    printf
		add     rsp, 16 //free memory from count_pi
		mov     eax, 0 //load return num
		leave
		ret
.LC4:
.long   0
.long   -2147483648
.long   16385
.long   0
.LC5:
.long   0
.long   -2147483648
.long   16447
.long   0