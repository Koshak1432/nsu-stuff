check_one(unsigned long long):
		and     edi, 1
		fld1
		je      .L3
		fchs
		ret
.L3:
		ret
count_pi(unsigned long long):
		test    rdi, rdi
		je      .L10
		mov     edx, 1
		fldz
		xor     eax, eax
		jmp     .L9
.L13:
		fld     DWORD PTR .LC5[rip]
.L7:
		mov     QWORD PTR [rsp-16], rdx
		fild    QWORD PTR [rsp-16]
		test    rdx, rdx
		jns     .L8
		fadd    DWORD PTR .LC6[rip]
.L8:
		fdivp   st(1), st
		add     rax, 1
		add     rdx, 2
		faddp   st(1), st
		cmp     rdi, rax
		je      .L5
.L9:
		test    al, 1
		jne     .L13
		fld     DWORD PTR .LC4[rip]
		jmp     .L7
.L10:
		fldz
.L5:
		ret
.LC7:
		.string "result : %.20Lf\n"
main:
		movabs  rdi, 9000000000
		sub     rsp, 8
		call    count_pi(unsigned long long)
		sub     rsp, 16
		mov     edi, OFFSET FLAT:.LC7
		xor     eax, eax
		fstp    TBYTE PTR [rsp]
		call    printf
		xor     eax, eax
		add     rsp, 24
ret
.LC4:
.long   1082130432 //4
.LC5:
.long   -1065353216 //-1
.LC6:
.long   1602224128