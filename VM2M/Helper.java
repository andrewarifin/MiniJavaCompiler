
public class Helper {
	public void print(String s, Boolean id) {
		if (id) System.out.println("  "+s);
		else System.out.println(s);
	}
	public void printHelper() {
		System.out.println("_print: \n"
				+ "li $v0 1\n"
				+ "syscall\n"
				+ "la $a0 _newline\n"
				+ "li $v0 4\n"
				+ "syscall\n"
				+ "jr $ra\n\n"
				+ "_error:\n"
				+ "li $v0 4\n"
				+ "syscall\n"
				+ "li $v0 10\n"
				+ "syscall\n\n"
				+ "_heapAlloc:\n"
				+ "li $v0 9\n"
				+ "syscall\n"
				+ "jr $ra\n\n"
				+ ".data\n"
				+ ".align 0\n"
				+ "_newline: .asciiz \"\\n\"\n"
				+ "_str0: .asciiz \"null pointer\\n\"\n"
				+ "_str1: .asciiz \"array index out of bounds\\n\"\n");
	}
	public void printHead(int s) {
		System.out.print("  sw $fp -8($sp)\n"
				+ "  move $fp $sp\n"
				+ "  subu $sp $sp "+(s+2)*4+"\n"
				+ "  sw $ra -4($fp)\n");
	}
	public void printTail(int s) {
		System.out.print("  lw $ra -4($fp)\n"
				+ "  lw $fp -8($fp)\n"
				+ "  addu $sp $sp "+(s+2)*4+"\n");
	}
}
