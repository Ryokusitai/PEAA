package peaa.asm.transform;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.*;

import peaa.asm.PEAACoreCorePlugin;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

/**
 * ObjHandlerで参照しているアイテム、ブロック、タイルエンティティの内のいくつかの中身を
 * こちらで書き換えたものに差し替えるための処理
 */
public class ObjHandlerTransformer implements IClassTransformer, Opcodes
{
	private static final String TARGETCLASSNAME = "moze_intel.projecte.gameObjs.ObjHandler";

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(!TARGETCLASSNAME.equals(transformedName)) {return basicClass;}

		try {
			PEAACoreCorePlugin.logger.info("-------------------------Start ObjHandler Transform--------------------------");
			ClassReader cr = new ClassReader(basicClass);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cr.accept(new CustomVisitor(name, cw, transformedName), 8);
			basicClass = cw.toByteArray();
			PEAACoreCorePlugin.logger.info("-------------------------Finish ObjHandler Transform-------------------------");
		} catch (Exception e) {
			throw new RuntimeException("failed : ObjHandlerTransformer loading", e);
		}
		return basicClass;
	}

	class CustomVisitor extends ClassVisitor
	{
		String owner;

		public CustomVisitor(String owner, ClassVisitor cv, String transformedName)
		{
			super(Opcodes.ASM4, cv);
			this.owner = owner;
		}

		static final String targetMethodName = "<clinit>";
		static final String targetMethodName2 = "register";

		/**
		 * ここでどのメソッドかを判断してそれぞれの書き換え処理に飛ばしている
		 */
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			// ObjHandlerのフィールド書き換え
			if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))) {
				PEAACoreCorePlugin.logger.info("Start [clinit] Transform");
				return new CustomMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));
			}

			// ObjHandlerのregisterにTileEntityの追加
			if (targetMethodName2.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))) {
				PEAACoreCorePlugin.logger.info("Add TileEntity [Mk2PEAA]");
				PEAACoreCorePlugin.logger.info("Add Entity [EntityWarterProjectilePEAA]");
				PEAACoreCorePlugin.logger.info("Add TileEntity [RMFurnaceTilePEAA]");
				return new CustomMethodVisitor2(this.api, super.visitMethod(access, name, desc, signature, exceptions));
			}

			return super.visitMethod(access, name, desc, signature, exceptions);
		}
	}

	/**
	 * [clinit]
	 * ObjHanlderの
	 * public static Block condenserMk2 = new CondenserMK2();
	 * を
	 * public static Block condenserMk2 = new CondenserMK2PEAA();
	 * に変更
	 *
	 * MatterFurnace から MatterFurnacePEAA
	 * に変更
	 *
	 * 水の祭器も
	 * EvertideAmulet から EvertideAmuletPEAA
	 * に変更
	 *
	 * GemFeetもGemFeetPEAAに変更
	 */
	class CustomMethodVisitor extends MethodVisitor {
		static final String targetFieldName = "condenserMk2";
		static final String targetFieldName2 = "rmFurnaceOff";
		static final String targetFieldName3 = "rmFurnaceOn";
		static final String targetFieldName4 = "everTide";
		static final String targetFieldName5 = "gemFeet";

		public CustomMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			// CondenserMK2PEAAに変更
			if (targetFieldName.equals(name)) {
				PEAACoreCorePlugin.logger.info("Start [condenserMk2] Transform");
				mv.visitTypeInsn(NEW, "peaa/gameObjs/blocks/CondenserMK2PEAA");
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, "peaa/gameObjs/blocks/CondenserMK2PEAA", "<init>", "()V", false);

			}
			// RMTilePEAAに変更(offかまど)
			if (targetFieldName2.equals(name)) {
				PEAACoreCorePlugin.logger.info("Start [MatterFurnace_Off] Transform");
				mv.visitTypeInsn(NEW, "peaa/gameObjs/blocks/MatterFurnacePEAA");
				mv.visitInsn(DUP);
				mv.visitInsn(ICONST_0);
				mv.visitInsn(ICONST_1);
				mv.visitMethodInsn(INVOKESPECIAL, "peaa/gameObjs/blocks/MatterFurnacePEAA", "<init>", "(ZZ)V", false);

			}
			// RMTilePEAAに変更(onかまど)
			if (targetFieldName3.equals(name)) {
				PEAACoreCorePlugin.logger.info("Start [MatterFurnace_On] Transform");
				mv.visitTypeInsn(NEW, "peaa/gameObjs/blocks/MatterFurnacePEAA");
				mv.visitInsn(DUP);
				mv.visitInsn(ICONST_1);
				mv.visitInsn(ICONST_1);
				mv.visitMethodInsn(INVOKESPECIAL, "peaa/gameObjs/blocks/MatterFurnacePEAA", "<init>", "(ZZ)V", false);

			}
			// EvertideAmuletPEAAに変更
			if (targetFieldName4.equals(name)) {
				PEAACoreCorePlugin.logger.info("Start [EvertideAmulet] Transform");
				mv.visitTypeInsn(NEW, "peaa/gameObjs/items/EvertideAmuletPEAA");
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, "peaa/gameObjs/items/EvertideAmuletPEAA", "<init>", "()V", false);

			}
			// GemFeetPEAAに変更
			if (targetFieldName5.equals(name)) {
				PEAACoreCorePlugin.logger.info("Start [GemFeet] Transform");
				mv.visitTypeInsn(NEW, "peaa/gameObjs/items/armor/GemFeetPEAA");
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, "peaa/gameObjs/items/armor/GemFeetPEAA", "<init>", "()V", false);
			}

			super.visitFieldInsn(opcode, owner, name, desc);
		}
	}

	/**
	 * [register]
	 * ObjHandlerに
	 * 		GameRegistry.registerTileEntity(CondenserMK2TilePEAA.class, "CondenserMK2 Tile  PEAA");
	 * を追加
	 *
	 * 水のオーブはPEAAで書き換えたものに変更
	 * mv.visitLdcInsn(Type.getType("Lpeaa/gameObjs/entity/EntityWaterProjectilePEAA;"));
	 */
	class CustomMethodVisitor2 extends MethodVisitor {
		public CustomMethodVisitor2(int api, MethodVisitor mv) {
            super(api, mv);
        }

		@Override
		public void visitCode() {
			super.visitCode();
			// ここから追加処理
			// add MK2Tile
			mv.visitLdcInsn(Type.getType("Lpeaa/gameObjs/tiles/CondenserMK2TilePEAA;"));
			mv.visitLdcInsn("CondenserMK2 Tile  PEAA");
			mv.visitMethodInsn(INVOKESTATIC, "cpw/mods/fml/common/registry/GameRegistry", "registerTileEntity", "(Ljava/lang/Class;Ljava/lang/String;)V", false);

			// add RMFurnaceTilePEAA
			mv.visitLdcInsn(Type.getType("Lpeaa/gameObjs/tiles/RMFurnaceTilePEAA;"));
			mv.visitLdcInsn("RM Furnace Tile PEAA");
			mv.visitMethodInsn(INVOKESTATIC, "cpw/mods/fml/common/registry/GameRegistry", "registerTileEntity", "(Ljava/lang/Class;Ljava/lang/String;)V", false);

			// add EntityWaterProjectilePEAA
			mv.visitLdcInsn(Type.getType("Lpeaa/gameObjs/entity/EntityWaterProjectilePEAA;"));
			mv.visitLdcInsn("Water Water PEAA");
			mv.visitIntInsn(BIPUSH, 10);
			mv.visitFieldInsn(GETSTATIC, "moze_intel/projecte/PECore", "instance", "Lmoze_intel/projecte/PECore;");
			mv.visitIntInsn(SIPUSH, 256);
			mv.visitIntInsn(BIPUSH, 10);
			mv.visitInsn(ICONST_1);
			mv.visitMethodInsn(INVOKESTATIC, "cpw/mods/fml/common/registry/EntityRegistry", "registerModEntity", "(Ljava/lang/Class;Ljava/lang/String;ILjava/lang/Object;IIZ)V", false);
		}

		@Override
		public void visitLdcInsn(Object cst) {
			if (Type.getType("Lmoze_intel/projecte/gameObjs/entity/EntityWaterProjectile;").equals(cst)) {
				mv.visitLdcInsn(Type.getType("Lpeaa/gameObjs/entity/EntityWaterProjectilePEAA;"));
				return;
			}
			super.visitLdcInsn(cst);
		}
	}
}
