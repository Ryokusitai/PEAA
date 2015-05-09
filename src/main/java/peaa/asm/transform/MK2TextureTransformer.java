package peaa.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.*;

import peaa.asm.PEAACoreCorePlugin;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

/**
 * MK2コンデンサーのアイテムとブロックのテクスチャを変更する
 *
 */
public class MK2TextureTransformer implements IClassTransformer, Opcodes
{
	private static final String TARGETCLASSNAME = "moze_intel.projecte.rendering.CondenserMK2ItemRenderer";
	private static final String TARGETCLASSNAME2 = "moze_intel.projecte.rendering.CondenserMK2Renderer";


	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(!TARGETCLASSNAME.equals(transformedName) && !TARGETCLASSNAME2.equals(transformedName)) {return basicClass;}

		try {
			PEAACoreCorePlugin.logger.info("-------------------------Start MK2Texture Transform--------------------------");
			ClassReader cr = new ClassReader(basicClass);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cr.accept(new CustomVisitor(name, cw, transformedName), 8);
			basicClass = cw.toByteArray();
			PEAACoreCorePlugin.logger.info("-------------------------Finish MK2Texture Transform-------------------------");
		} catch (Exception e) {
			throw new RuntimeException("failed : MK2Texture loading", e);
		}
		return basicClass;
	}

	class CustomVisitor extends ClassVisitor
	{
		String owner;
		String transformedName;
		public CustomVisitor(String owner, ClassVisitor cv, String transformedName)
		{
			super(Opcodes.ASM4, cv);
			this.owner = owner;
			this.transformedName = transformedName;
		}

		static final String targetMethodName = "<init>";

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			// ItemIcon変更
			if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))) {
				PEAACoreCorePlugin.logger.info("Start [init] Transform");
				return new CustomMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));

			}

			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}

	/**
	 * テクスチャの参照場所をProjectEからPEAAに変えるだけ
	 */
	class CustomMethodVisitor extends MethodVisitor {
		int cnt = 0;

		public CustomMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

		@Override
		public void visitLdcInsn(Object cst) {
			// 最初の1つが書き換えたいメソッド
			if (cnt == 0) {
				super.visitLdcInsn("PEAA");
				cnt++;
				return;
			}
			super.visitLdcInsn(cst);
		}
	}

}
