package peaa.asm.transform;

import org.objectweb.asm.*;

import peaa.asm.PEAACoreCorePlugin;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * 上手く動かない為未実装
 */
public class ToolTipEventTransformer implements IClassTransformer, Opcodes
{
	private static final String TARGETCLASSNAME = "moze_intel.projecte.events.ToolTipEvent";

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (!TARGETCLASSNAME.equals(transformedName)) { return basicClass;}

		try {
			PEAACoreCorePlugin.logger.info("-------------------------Start ToolTipEvent Transform--------------------------");
			ClassReader cr = new ClassReader(basicClass);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cr.accept(new CustomVisitor(name, cw), 8);
			basicClass = cw.toByteArray();
			PEAACoreCorePlugin.logger.info("-------------------------Finish ToolTipEvent Transform--------------------------");
		} catch (Exception e) {
			throw new RuntimeException("failed : ToolTipEvent loading", e);
		}
		return basicClass;
	}

	class CustomVisitor extends ClassVisitor
	{
		String owner;
		public CustomVisitor(String owner, ClassVisitor cv)
		{
			super(Opcodes.ASM4, cv);
			this.owner = owner;
		}

		static final String targetMethodName = "tTipEvent";

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			// キャスト追加
			if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))) {
				PEAACoreCorePlugin.logger.info("Start [tTipEvent] Transform");
				return new CustomMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
			}

			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}

	int cntVarInsn = 0;
	boolean finishCast;
	boolean finishChange;
	/**
	 * longキャストを追加
	 */
	class CustomMethodVisitor extends MethodVisitor {
		public CustomMethodVisitor(int api, MethodVisitor mv) {
			super(api, mv);
		}

		@Override
		public void visitVarInsn(int opcode, int var) {
			super.visitVarInsn(opcode, var);
			if (opcode == ILOAD && var == 5)
				cntVarInsn++;

			if (cntVarInsn == 3 && !finishCast) {	// 3回目の呼び出しなら
				System.out.println("add-------------------------------------------------------");
				mv.visitInsn(I2L);	// キャストを追加
				finishCast = true;
			}
		}

		@Override
		public void visitInsn(int opcode) {
			if (finishCast && !finishChange) {
				if (opcode == IMUL) {
					super.visitInsn(I2L);
					return;
				}
				if (opcode == I2L) {
					super.visitInsn(LMUL);
					finishChange = true;
					return;
				}
			}

			super.visitInsn(opcode);
		}
	}

}
