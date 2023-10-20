	.file	"syscalls.c"
	.option nopic
	.attribute arch, "rv64i2p0_m2p0_a2p0_f2p0_d2p0_c2p0"
	.attribute unaligned_access, 0
	.attribute stack_align, 16
	.text
	.section	.rodata.str1.8,"aMS",@progbits,1
	.align	3
.LC0:
	.string	"(null)"
	.text
	.align	1
	.align	2
	.type	vprintfmt, @function
vprintfmt:
	addi	sp,sp,-352
	sd	s0,336(sp)
	sd	s1,328(sp)
	sd	s2,320(sp)
	sd	s5,296(sp)
	sd	s6,288(sp)
	sd	s7,280(sp)
	sd	ra,344(sp)
	sd	s3,312(sp)
	sd	s4,304(sp)
	sd	s8,272(sp)
	sd	s9,264(sp)
	sd	s10,256(sp)
	mv	s2,a0
	mv	s1,a1
	mv	s0,a2
	mv	s6,a3
	li	s5,37
	lla	s7,.L9
	j	.L80
.L5:
	beq	a0,zero,.L78
	mv	a1,s1
	addi	s0,s0,1
	jalr	s2
.L80:
	lbu	a0,0(s0)
	bne	a0,s5,.L5
	lbu	a3,1(s0)
	addi	s4,s0,1
	mv	a4,s4
	li	s9,32
	li	s3,-1
	li	s10,-1
	li	a0,0
	li	a2,85
	li	a1,9
.L6:
	addiw	a5,a3,-35
	andi	a5,a5,0xff
	addi	s0,a4,1
	bgtu	a5,a2,.L7
.L85:
	slli	a5,a5,2
	add	a5,a5,s7
	lw	a5,0(a5)
	add	a5,a5,s7
	jr	a5
	.section	.rodata
	.align	2
	.align	2
.L9:
	.word	.L23-.L9
	.word	.L7-.L9
	.word	.L22-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L21-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L18-.L9
	.word	.L19-.L9
	.word	.L7-.L9
	.word	.L18-.L9
	.word	.L17-.L9
	.word	.L17-.L9
	.word	.L17-.L9
	.word	.L17-.L9
	.word	.L17-.L9
	.word	.L17-.L9
	.word	.L17-.L9
	.word	.L17-.L9
	.word	.L17-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L16-.L9
	.word	.L15-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L14-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L63-.L9
	.word	.L12-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L11-.L9
	.word	.L7-.L9
	.word	.L10-.L9
	.word	.L7-.L9
	.word	.L7-.L9
	.word	.L8-.L9
	.text
.L78:
	ld	ra,344(sp)
	ld	s0,336(sp)
	ld	s1,328(sp)
	ld	s2,320(sp)
	ld	s3,312(sp)
	ld	s4,304(sp)
	ld	s5,296(sp)
	ld	s6,288(sp)
	ld	s7,280(sp)
	ld	s8,272(sp)
	ld	s9,264(sp)
	ld	s10,256(sp)
	addi	sp,sp,352
	jr	ra
.L18:
	mv	s9,a3
	lbu	a3,1(a4)
	mv	a4,s0
	addi	s0,a4,1
	addiw	a5,a3,-35
	andi	a5,a5,0xff
	bleu	a5,a2,.L85
.L7:
	mv	a1,s1
	li	a0,37
	jalr	s2
	mv	s0,s4
	j	.L80
.L17:
	addiw	s3,a3,-48
	lbu	a3,1(a4)
	mv	a4,s0
	addiw	a5,a3,-48
	sext.w	a6,a3
	bgtu	a5,a1,.L25
.L26:
	lbu	a3,1(a4)
	slliw	a5,s3,2
	addw	s3,a5,s3
	slliw	s3,s3,1
	addw	s3,s3,a6
	addiw	a5,a3,-48
	addi	a4,a4,1
	addiw	s3,s3,-48
	sext.w	a6,a3
	bleu	a5,a1,.L26
.L25:
	bge	s10,zero,.L6
	mv	s10,s3
	li	s3,-1
	j	.L6
.L23:
	lbu	a3,1(a4)
	mv	a4,s0
	j	.L6
.L22:
	mv	a1,s1
	li	a0,37
	jalr	s2
	j	.L80
.L21:
	lw	s3,0(s6)
	lbu	a3,1(a4)
	addi	s6,s6,8
	mv	a4,s0
	j	.L25
.L12:
	li	a0,48
	mv	a1,s1
	jalr	s2
	mv	a1,s1
	li	a0,120
	jalr	s2
	addi	a3,s6,8
	li	a4,16
.L47:
	ld	a5,0(s6)
	mv	s6,a3
.L46:
	remu	s3,a5,a4
	sext.w	s9,s9
	addi	a3,sp,4
	li	s8,1
	sw	s3,0(sp)
	bltu	a5,a4,.L86
.L55:
	divu	a5,a5,a4
	addi	a3,a3,4
	mv	s4,s8
	addiw	s8,s8,1
	remu	s3,a5,a4
	sw	s3,-4(a3)
	bgeu	a5,a4,.L55
	ble	s10,s8,.L52
.L51:
	sext.w	s10,s10
.L56:
	addiw	s10,s10,-1
	mv	a1,s1
	mv	a0,s9
	jalr	s2
	blt	s8,s10,.L56
.L52:
	slli	s4,s4,2
	add	s4,sp,s4
	mv	s9,sp
	li	s8,9
	j	.L59
.L87:
	lw	s3,-4(s4)
	addi	s4,s4,-4
.L59:
	li	a0,87
	bgtu	s3,s8,.L57
	li	a0,48
.L57:
	mv	a1,s1
	addw	a0,a0,s3
	jalr	s2
	bne	s4,s9,.L87
	j	.L80
.L15:
	li	a5,1
	addi	s4,s6,8
	bgt	a0,a5,.L82
	beq	a0,zero,.L44
.L82:
	ld	s3,0(s6)
.L43:
	blt	s3,zero,.L45
	mv	a5,s3
	mv	s6,s4
	li	a4,10
	j	.L46
.L14:
	lbu	a3,1(a4)
	addiw	a0,a0,1
	mv	a4,s0
	j	.L6
.L8:
	li	a4,16
.L13:
	li	a5,1
	addi	a3,s6,8
	bgt	a0,a5,.L47
	bne	a0,zero,.L47
	lwu	a5,0(s6)
	mv	s6,a3
	j	.L46
.L11:
	ld	s4,0(s6)
	addi	s6,s6,8
	beq	s4,zero,.L29
	ble	s10,zero,.L30
	li	a5,45
	bne	s9,a5,.L62
	lbu	a0,0(s4)
	beq	a0,zero,.L41
	li	s8,-1
.L37:
	blt	s3,zero,.L39
	addiw	s3,s3,-1
	beq	s3,s8,.L40
.L39:
	mv	a1,s1
	addi	s4,s4,1
	jalr	s2
	lbu	a0,0(s4)
	addiw	s10,s10,-1
	bne	a0,zero,.L37
.L40:
	ble	s10,zero,.L80
.L41:
	addiw	s10,s10,-1
	mv	a1,s1
	li	a0,32
	jalr	s2
	bne	s10,zero,.L41
	j	.L80
.L69:
	lla	s4,.LC0
.L62:
	sext.w	a2,s10
	mv	a5,s4
	add	a3,s4,s3
	bne	s3,zero,.L32
	j	.L33
.L35:
	addi	a5,a5,1
	beq	a5,a3,.L81
.L32:
	lbu	a4,0(a5)
	bne	a4,zero,.L35
.L81:
	sub	s10,a5,s4
	subw	s10,a2,s10
	bgt	s10,zero,.L33
.L30:
	lbu	a0,0(s4)
	beq	a0,zero,.L80
	li	s8,-1
	j	.L37
.L19:
	not	a5,s10
	srai	a5,a5,63
	and	s10,s10,a5
	lbu	a3,1(a4)
	sext.w	s10,s10
	mv	a4,s0
	j	.L6
.L16:
	lw	a0,0(s6)
	mv	a1,s1
	addi	s6,s6,8
	jalr	s2
	j	.L80
.L86:
	li	a5,1
	li	s4,0
	bgt	s10,a5,.L51
	j	.L52
.L44:
	lw	s3,0(s6)
	j	.L43
.L63:
	li	a4,8
	j	.L13
.L10:
	li	a4,10
	j	.L13
.L45:
	mv	a1,s1
	li	a0,45
	jalr	s2
	mv	s6,s4
	neg	a5,s3
	li	a4,10
	j	.L46
.L29:
	ble	s10,zero,.L68
	li	a5,45
	bne	s9,a5,.L69
	li	a0,40
	lla	s4,.LC0
	li	s8,-1
	j	.L37
.L33:
	sext.w	s9,s9
.L36:
	addiw	s10,s10,-1
	mv	a1,s1
	mv	a0,s9
	jalr	s2
	bne	s10,zero,.L36
	j	.L30
.L68:
	lla	s4,.LC0
	li	a0,40
	li	s8,-1
	j	.L37
	.size	vprintfmt, .-vprintfmt
	.align	1
	.align	2
	.type	sprintf_putch.3166, @function
sprintf_putch.3166:
	ld	a5,0(a1)
	sb	a0,0(a5)
	ld	a5,0(a1)
	addi	a5,a5,1
	sd	a5,0(a1)
	ret
	.size	sprintf_putch.3166, .-sprintf_putch.3166
	.align	1
	.align	2
	.globl	putchar
	.type	putchar, @function
putchar:
	lui	a2,%tprel_hi(buflen.3052)
	add	a1,a2,tp,%tprel_add(buflen.3052)
	lw	a6,%tprel_lo(buflen.3052)(a1)
	lui	a3,%tprel_hi(.LANCHOR0)
	add	a5,a3,tp,%tprel_add(.LANCHOR0)
	addi	a5,a5,%tprel_lo(.LANCHOR0)
	add	a5,a5,a6
	addi	sp,sp,-112
	addiw	a4,a6,1
	sw	a4,%tprel_lo(buflen.3052)(a1)
	sb	a0,0(a5)
	li	a1,10
	addi	a5,sp,63
	andi	a5,a5,-64
	beq	a0,a1,.L90
	li	a1,64
	beq	a4,a1,.L90
	li	a0,0
	addi	sp,sp,112
	jr	ra
.L90:
	li	a1,64
	sd	a1,0(a5)
	add	a3,a3,tp,%tprel_add(.LANCHOR0)
	li	a1,1
	sd	a1,8(a5)
	addi	a3,a3,%tprel_lo(.LANCHOR0)
	sd	a3,16(a5)
	sd	a4,24(a5)
	fence	iorw,iorw
	lla	a3,fromhost
	sd	a5,tohost,a4
.L92:
	ld	a4,0(a3)
	beq	a4,zero,.L92
	sd	zero,fromhost,a4
	fence	iorw,iorw
	add	a2,a2,tp,%tprel_add(buflen.3052)
	sw	zero,%tprel_lo(buflen.3052)(a2)
	ld	a5,0(a5)
	li	a0,0
	addi	sp,sp,112
	jr	ra
	.size	putchar, .-putchar
	.section	.rodata.str1.8
	.align	3
.LC1:
	.string	"mcycle"
	.align	3
.LC2:
	.string	"minstret"
	.text
	.align	1
	.align	2
	.globl	setStats
	.type	setStats, @function
setStats:
 #APP
# 51 "syscalls.c" 1
	csrr a4, mcycle
# 0 "" 2
 #NO_APP
	lla	a5,.LANCHOR1
	bne	a0,zero,.L97
	ld	a3,0(a5)
	lla	a2,.LC1
	sd	a2,16(a5)
	sub	a4,a4,a3
.L97:
	sd	a4,0(a5)
 #APP
# 52 "syscalls.c" 1
	csrr a4, minstret
# 0 "" 2
 #NO_APP
	bne	a0,zero,.L98
	ld	a3,8(a5)
	lla	a2,.LC2
	sd	a2,24(a5)
	sub	a4,a4,a3
.L98:
	sd	a4,8(a5)
	ret
	.size	setStats, .-setStats
	.align	1
	.align	2
	.globl	tohost_exit
	.type	tohost_exit, @function
tohost_exit:
	slli	a5,a0,1
	ori	a5,a5,1
	sd	a5,tohost,a4
.L101:
	j	.L101
	.size	tohost_exit, .-tohost_exit
	.align	1
	.align	2
	.weak	handle_trap
	.type	handle_trap, @function
handle_trap:
	li	a5,4096
	addi	a5,a5,-1421
	sd	a5,tohost,a4
.L103:
	j	.L103
	.size	handle_trap, .-handle_trap
	.align	1
	.align	2
	.globl	exit
	.type	exit, @function
exit:
	addi	sp,sp,-16
	sd	ra,8(sp)
	call	tohost_exit
	.size	exit, .-exit
	.align	1
	.align	2
	.globl	abort
	.type	abort, @function
abort:
	li	a5,269
	sd	a5,tohost,a4
.L107:
	j	.L107
	.size	abort, .-abort
	.align	1
	.align	2
	.globl	printstr
	.type	printstr, @function
printstr:
	lbu	a5,0(a0)
	addi	sp,sp,-112
	addi	a3,sp,63
	andi	a3,a3,-64
	beq	a5,zero,.L112
	mv	a5,a0
.L110:
	lbu	a4,1(a5)
	addi	a5,a5,1
	bne	a4,zero,.L110
	sub	a5,a5,a0
.L109:
	li	a4,64
	sd	a4,0(a3)
	li	a4,1
	sd	a4,8(a3)
	sd	a0,16(a3)
	sd	a5,24(a3)
	fence	iorw,iorw
	lla	a4,fromhost
	sd	a3,tohost,a5
.L111:
	ld	a5,0(a4)
	beq	a5,zero,.L111
	sd	zero,fromhost,a5
	fence	iorw,iorw
	ld	a5,0(a3)
	addi	sp,sp,112
	jr	ra
.L112:
	li	a5,0
	j	.L109
	.size	printstr, .-printstr
	.align	1
	.align	2
	.weak	thread_entry
	.type	thread_entry, @function
thread_entry:
.L118:
	bne	a0,zero,.L118
	ret
	.size	thread_entry, .-thread_entry
	.section	.rodata.str1.8
	.align	3
.LC3:
	.string	"Implement main(), foo!\n"
	.section	.text.startup,"ax",@progbits
	.align	1
	.align	2
	.weak	main
	.type	main, @function
main:
	addi	sp,sp,-16
	lla	a0,.LC3
	sd	ra,8(sp)
	call	printstr
	ld	ra,8(sp)
	li	a0,-1
	addi	sp,sp,16
	jr	ra
	.size	main, .-main
	.text
	.align	1
	.align	2
	.globl	printhex
	.type	printhex, @function
printhex:
	addi	sp,sp,-48
	mv	a5,a0
	sd	ra,40(sp)
	addi	a0,sp,8
	addi	a2,sp,23
	li	a6,9
	j	.L124
.L126:
	mv	a2,a4
.L124:
	andi	a3,a5,15
	mv	a4,a3
	li	a1,48
	bleu	a3,a6,.L123
	li	a1,87
.L123:
	addw	a4,a4,a1
	sb	a4,0(a2)
	srli	a5,a5,4
	addi	a4,a2,-1
	bne	a0,a2,.L126
	sb	zero,24(sp)
	call	printstr
	ld	ra,40(sp)
	addi	sp,sp,48
	jr	ra
	.size	printhex, .-printhex
	.align	1
	.align	2
	.globl	printf
	.type	printf, @function
printf:
	addi	sp,sp,-96
	addi	t1,sp,40
	mv	t3,a0
	sd	a1,40(sp)
	sd	a2,48(sp)
	sd	a3,56(sp)
	lla	a0,putchar
	mv	a3,t1
	mv	a2,t3
	li	a1,0
	sd	ra,24(sp)
	sd	a4,64(sp)
	sd	a5,72(sp)
	sd	a6,80(sp)
	sd	a7,88(sp)
	sd	t1,8(sp)
	call	vprintfmt
	ld	ra,24(sp)
	li	a0,0
	addi	sp,sp,96
	jr	ra
	.size	printf, .-printf
	.align	1
	.align	2
	.globl	sprintf
	.type	sprintf, @function
sprintf:
	addi	sp,sp,-96
	addi	t1,sp,48
	sd	s0,32(sp)
	sd	a0,8(sp)
	sd	a2,48(sp)
	sd	a3,56(sp)
	mv	s0,a0
	mv	a2,a1
	lla	a0,sprintf_putch.3166
	addi	a1,sp,8
	mv	a3,t1
	sd	ra,40(sp)
	sd	a5,72(sp)
	sd	a4,64(sp)
	sd	a6,80(sp)
	sd	a7,88(sp)
	sd	t1,24(sp)
	call	vprintfmt
	ld	a5,8(sp)
	sb	zero,0(a5)
	ld	a0,8(sp)
	ld	ra,40(sp)
	subw	a0,a0,s0
	ld	s0,32(sp)
	addi	sp,sp,96
	jr	ra
	.size	sprintf, .-sprintf
	.align	1
	.align	2
	.globl	memcpy
	.type	memcpy, @function
memcpy:
	or	a5,a1,a2
	or	a5,a0,a5
	andi	a5,a5,7
	add	a3,a0,a2
	beq	a5,zero,.L133
	add	a2,a1,a2
	mv	a5,a0
	bleu	a3,a0,.L142
.L137:
	lbu	a4,0(a1)
	addi	a1,a1,1
	addi	a5,a5,1
	sb	a4,-1(a5)
	bne	a2,a1,.L137
.L138:
	ret
.L133:
	bleu	a3,a0,.L138
	mv	a5,a0
.L136:
	ld	a4,0(a1)
	addi	a5,a5,8
	addi	a1,a1,8
	sd	a4,-8(a5)
	bgtu	a3,a5,.L136
	ret
.L142:
	ret
	.size	memcpy, .-memcpy
	.align	1
	.align	2
	.globl	memset
	.type	memset, @function
memset:
	or	a5,a0,a2
	andi	a5,a5,7
	add	a2,a0,a2
	andi	a1,a1,0xff
	beq	a5,zero,.L144
	mv	a5,a0
	bleu	a2,a0,.L153
.L148:
	addi	a5,a5,1
	sb	a1,-1(a5)
	bne	a2,a5,.L148
.L149:
	ret
.L144:
	slli	a5,a1,8
	or	a5,a1,a5
	slli	a1,a5,16
	or	a5,a5,a1
	slli	a4,a5,32
	or	a5,a5,a4
	bleu	a2,a0,.L149
	mv	a4,a0
.L147:
	addi	a4,a4,8
	sd	a5,-8(a4)
	bgtu	a2,a4,.L147
	ret
.L153:
	ret
	.size	memset, .-memset
	.section	.rodata.str1.8
	.align	3
.LC4:
	.string	"%s = %lu\n"
	.text
	.align	1
	.align	2
	.globl	_init
	.type	_init, @function
_init:
	addi	sp,sp,-176
	sd	s0,160(sp)
	sd	s1,152(sp)
	lui	s0,%tprel_hi(_tdata_begin)
	lui	s1,%tprel_hi(_tdata_end)
	add	s1,s1,tp,%tprel_add(_tdata_end)
	add	s0,s0,tp,%tprel_add(_tdata_begin)
	addi	s1,s1,%tprel_lo(_tdata_end)
	addi	s0,s0,%tprel_lo(_tdata_begin)
	sub	s0,s1,s0
	mv	a2,s0
	sd	s3,136(sp)
	sd	s4,128(sp)
	mv	s3,a0
	mv	s4,a1
	mv	a0,tp
	lla	a1,_tls_data
	sd	ra,168(sp)
	sd	s2,144(sp)
	sd	s5,120(sp)
	mv	s5,tp
	call	memcpy
	lui	a2,%tprel_hi(_tbss_end)
	add	a2,a2,tp,%tprel_add(_tbss_end)
	addi	a2,a2,%tprel_lo(_tbss_end)
	sub	a2,a2,s1
	li	a1,0
	add	a0,s5,s0
	call	memset
	mv	a1,s4
	mv	a0,s3
	call	thread_entry
	li	a1,0
	li	a0,0
	call	main
	lla	s1,.LANCHOR1
	ld	a3,0(s1)
	addi	s2,sp,63
	andi	s2,s2,-64
	mv	s0,a0
	bne	a3,zero,.L166
	ld	a3,8(s1)
	bne	a3,zero,.L167
.L158:
	mv	a0,s0
	call	tohost_exit
.L167:
	mv	s3,s2
.L159:
	ld	a2,24(s1)
	mv	a0,s3
	lla	a1,.LC4
	call	sprintf
	add	s3,s3,a0
.L156:
	beq	s2,s3,.L158
	mv	a0,s2
	call	printstr
	j	.L158
.L166:
	ld	a2,16(s1)
	lla	a1,.LC4
	mv	a0,s2
	call	sprintf
	ld	a3,8(s1)
	add	s3,s2,a0
	beq	a3,zero,.L156
	j	.L159
	.size	_init, .-_init
	.align	1
	.align	2
	.globl	strlen
	.type	strlen, @function
strlen:
	lbu	a5,0(a0)
	beq	a5,zero,.L171
	mv	a5,a0
.L170:
	lbu	a4,1(a5)
	addi	a5,a5,1
	bne	a4,zero,.L170
	sub	a0,a5,a0
	ret
.L171:
	li	a0,0
	ret
	.size	strlen, .-strlen
	.align	1
	.align	2
	.globl	strnlen
	.type	strnlen, @function
strnlen:
	mv	a2,a0
	add	a3,a2,a1
	mv	a0,a1
	mv	a5,a2
	bne	a1,zero,.L176
	j	.L180
.L177:
	addi	a5,a5,1
	beq	a3,a5,.L175
.L176:
	lbu	a4,0(a5)
	bne	a4,zero,.L177
	sub	a0,a5,a2
.L175:
	ret
.L180:
	ret
	.size	strnlen, .-strnlen
	.align	1
	.align	2
	.globl	strcmp
	.type	strcmp, @function
strcmp:
.L183:
	lbu	a5,0(a0)
	addi	a1,a1,1
	addi	a0,a0,1
	lbu	a4,-1(a1)
	beq	a5,zero,.L184
	beq	a5,a4,.L183
	sext.w	a0,a5
.L182:
	subw	a0,a0,a4
	ret
.L184:
	li	a0,0
	j	.L182
	.size	strcmp, .-strcmp
	.align	1
	.align	2
	.globl	strcpy
	.type	strcpy, @function
strcpy:
	mv	a5,a0
.L187:
	lbu	a4,0(a1)
	addi	a5,a5,1
	addi	a1,a1,1
	sb	a4,-1(a5)
	bne	a4,zero,.L187
	ret
	.size	strcpy, .-strcpy
	.align	1
	.align	2
	.globl	atol
	.type	atol, @function
atol:
	lbu	a4,0(a0)
	li	a3,32
	mv	a5,a0
	bne	a4,a3,.L190
.L191:
	lbu	a4,1(a5)
	addi	a5,a5,1
	beq	a4,a3,.L191
.L190:
	li	a3,45
	beq	a4,a3,.L192
	li	a3,43
	beq	a4,a3,.L211
	lbu	a3,0(a5)
	li	a1,0
	beq	a3,zero,.L210
.L197:
	li	a0,0
.L195:
	addi	a5,a5,1
	addiw	a2,a3,-48
	slli	a4,a0,2
	lbu	a3,0(a5)
	add	a0,a4,a0
	slli	a0,a0,1
	add	a0,a2,a0
	bne	a3,zero,.L195
	beq	a1,zero,.L189
	neg	a0,a0
	ret
.L211:
	lbu	a3,1(a5)
	li	a1,0
	addi	a5,a5,1
	bne	a3,zero,.L197
.L210:
	li	a0,0
.L189:
	ret
.L192:
	lbu	a3,1(a5)
	li	a1,1
	addi	a5,a5,1
	bne	a3,zero,.L197
	li	a0,0
	j	.L189
	.size	atol, .-atol
	.bss
	.align	3
	.set	.LANCHOR1,. + 0
	.type	counters, @object
	.size	counters, 16
counters:
	.zero	16
	.type	counter_names, @object
	.size	counter_names, 16
counter_names:
	.zero	16
	.section	.tbss,"awT",@nobits
	.align	6
	.set	.LANCHOR0,. + 0
	.type	buf.3051, @object
	.size	buf.3051, 64
buf.3051:
	.zero	64
	.type	buflen.3052, @object
	.size	buflen.3052, 4
buflen.3052:
	.zero	4
	.ident	"GCC: (GNU) 9.2.0"
