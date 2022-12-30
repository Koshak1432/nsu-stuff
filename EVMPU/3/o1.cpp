check_one(unsigned long long):
		test    dil, 1 //bitwise and with the last 8 bit
		je      .L3 //if num % 2 == 0
		fld1
		fchs
		ret
		.L3:
		fld1
		ret
count_pi(unsigned long long):
		test    rdi, rdi //num
		je      .L12
		mov     edx, 1 //1 from (2 * i + 1)
		mov     eax, 0 //i = 0
		fldz	// 0
		fld1	// 1 0
		fld     DWORD PTR .LC4[rip]  // 4 1 0
		fld     DWORD PTR .LC5[rip] // 2^64 4 1 0
		fld     st(2)  // 1 2^64 4 1 0
		fld1     // 1 1 2^64 4 1 0
		fchs     // -1 1 2^4 4 1 0
		fxch    st(4)  // 1 1 2^64 4 -1 0
		jmp     .L5
.L12:
		fldz //load 0
		jmp     .L4
.L14:
		fstp    st(0)
		fstp    st(0)
		fstp    st(0)
		fstp    st(0)
.L4:
		ret
.L7:
		add     rdx, 2
.L5:
		fmul    st, st(3) // 4 1 2^64 4 -1 0
		mov     QWORD PTR [rsp-16], rdx
		fild    QWORD PTR [rsp-16] // 1(divisor) 4 1 2^64 4 -1 0
		test    rdx, rdx
		js      .L13
.L8:  // 1(divisor) 4 1 2^64 4 -1 0
		fdivp   st(1), st  // 4(res of div) 1 2^64 4 -1 0
		faddp   st(5), st  // 1 2^64 4 -1 4(res)
		add     rax, 1
		cmp     rdi, rax
		je      .L14
		fld     st(0)
		// 1 1 2^64 4 -1 4
		test    al, 1
		je      .L7
		fstp    st(0)  // 1 2^64 4 -1 4
		fld     st(3)  // -1 1 2^64 4 -1 4
		jmp     .L7
.L13:
		fadd    st, st(3) //normalize number by adding 2^64
		jmp     .L8
.LC6:
		.string "result : %.20Lf\n"
main:
		sub     rsp, 8 //save memory for N
		movabs  rdi, 9000000000 //move immediate value to register
		call    count_pi(unsigned long long)
		lea     rsp, [rsp-16] //save memory for result count_pi
		fstp    TBYTE PTR [rsp]
		mov     edi, OFFSET FLAT:.LC6
		mov     eax, 0
		call    printf
		mov     eax, 0
		add     rsp, 24
		ret
.LC4:
.long   1082130432
.LC5:
.long   1602224128