package peaa.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.*;

import peaa.asm.PEAACoreCorePlugin;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

/**
 * DMかまどにRMFurnaceTilePEAAを継承させるための書き換え処理
 *
 */
public class DMFurnaceTileTransformer implements IClassTransformer, Opcodes
{
	private static final String TARGETCLASSNAME = "moze_intel.projecte.gameObjs.tiles.DMFurnaceTile";

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass)
	{
		if (!TARGETCLASSNAME.equals(transformedName)) {return basicClass;}

		try {
			PEAACoreCorePlugin.logger.info("-------------------------Start DMFurnaceTile Transform--------------------------");
			ClassReader cr = new ClassReader(basicClass);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cr.accept(new CustomVisitor(name, cw), 8);
			basicClass = cw.toByteArray();
			PEAACoreCorePlugin.logger.info("-------------------------Finish DMFurnaceTile Transform--------------------------");
		} catch (Exception e) {
			throw new RuntimeException("failed : DMFurnaceTile Transformer loading", e);
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

		static final String targetMethodName = "<init>";

		/**
		 * ここでどのメソッドかを判断してそれぞれの書き換え処理に飛ばしている
		 */
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
		{
			// 2箇所書き換えないといけない内の1つ
			if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc)))
			{
				PEAACoreCorePlugin.logger.info("Transform DMFurnaceTile extends [RMFurnaceTilePEAA]");
				return new CustomMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
			}

			return super.visitMethod(access, name, desc, signature, exceptions);
		}

		String DMTileName = "moze_intel/projecte/gameObjs/tiles/DMFurnaceTile";
		/**
		 * 2箇所書き換えないといけない内の1つ
		 */
		@Override
		public void visit(int version, int access, String name, String signature,
				String superName, String[] interfaces)
		{
			if (name.equals(DMTileName))
			{
				super.visit(version, access, name, signature, "peaa/gameObjs/tiles/RMFurnaceTilePEAA", interfaces);
				return;
			}

			super.visit(version, access, name, signature, superName, interfaces);
		}

		class CustomMethodVisitor extends MethodVisitor
		{
			static final String targetVisitMethodInsnName = "moze_intel/projecte/gameObjs/tiles/RMFurnaceTile";

			public CustomMethodVisitor(int api, MethodVisitor mv)
			{
				super(api, mv);
			}

			@Override
			public void visitMethodInsn(int opcode, String owner, String name,
					String desc, boolean itf) {
				if (owner.equals(targetVisitMethodInsnName))
				{
					super.visitMethodInsn(opcode, "peaa/gameObjs/tiles/RMFurnaceTilePEAA", name, desc, itf);
					return;
				}
				super.visitMethodInsn(opcode, owner, name, desc, itf);
			}
		}
	}

}
