package peaa.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.*;

import peaa.asm.PEAACoreCorePlugin;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

/**
 * コンデンサーのプログレスバーが、一定EMC以上のアイテムの生産時に上手く表示されない問題を修正するための処理
 */
public class CondenserTileTransformer implements IClassTransformer, Opcodes
{
	private static final String TARGETCLASSNAME = "moze_intel.projecte.gameObjs.tiles.CondenserTile";

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		//if (!FMLLaunchHandler.side().isClient()) {return basicClass;}
		if(!TARGETCLASSNAME.equals(transformedName)) {return basicClass;}

		try {
			PEAACoreCorePlugin.logger.info("-------------------------Start CondenserTile Transform--------------------------");
			ClassReader cr = new ClassReader(basicClass);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cr.accept(new CustomVisitor(name, cw), 8);
			basicClass = cw.toByteArray();
			PEAACoreCorePlugin.logger.info("-------------------------Finish CondenserTile Transform-------------------------");
		} catch (Exception e) {
			throw new RuntimeException("failed : CondenserTileTransformer loading", e);
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

		static final String targetMethodName = "getProgressScaled";

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
		{
			// コンデンサーのプログレスバー表示の修正
			if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
			{
				PEAACoreCorePlugin.logger.info("Transform add cast to [CondenserTile]");
				return new CustomMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
			}

			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}

	/**
	 * PEAAのEmc量が想定の範囲外の為コンデンサーのプログレスバーの表示が上手くいかない問題に対応
	 * とはいってもLongのキャストを挟むだけ(数が22億くらいを超えてintの上限を突破しているのが原因なので)
	 *
	 * return (displayEmc * Constants.MAX_CONDENSER_PROGRESS) / requiredEmc;
	 * から
	 * return (int)( ((long)displayEmc * Constants.MAX_CONDENSER_PROGRESS) / requiredEmc);
	 * にするだけ
	 */
	class CustomMethodVisitor extends MethodVisitor
	{
		static final String targetVisitMethodInsnName = "moze_intel/projecte/gameObjs/tiles/RMFurnaceTile";
		int count = 0;

		public CustomMethodVisitor(int api, MethodVisitor mv)
		{
			super(api, mv);
		}

		/**
		 * 書き換えたい visitInsnは3つめ
		 */
		public void visitInsn(int opcode) {
			if (opcode == IRETURN)
				count++;

			if (count == 3)
			{
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, "moze_intel/projecte/gameObjs/tiles/CondenserTile", "displayEmc", "I");
				mv.visitInsn(I2L);
				mv.visitLdcInsn(new Long(102L));
				mv.visitInsn(LMUL);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, "moze_intel/projecte/gameObjs/tiles/CondenserTile", "requiredEmc", "I");
				mv.visitInsn(I2L);
				mv.visitInsn(LDIV);
				mv.visitInsn(L2I);
			}
			super.visitInsn(opcode);
		}
	}

}
