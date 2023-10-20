bmarkdir := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))

TARGET := riscv64-unknown-elf
CFLAGS := -mcmodel=medany -O2 -Wno-implicit -fno-common -fno-builtin-printf -Wall
LDFLAGS := -static -nostdlib -nostartfiles -lgcc

PROGRAMS := hello_world

objs := crt.o syscalls.o
ldscript := link.ld

include $(bmarkdir)/../common.mk
