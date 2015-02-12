package peaa.asm;


import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class PEAACoreTransformer implements IClassTransformer, Opcodes
{
	private static final String TARGETCLASSNAME = "moze_intel.projecte.gameObjs.ObjHandler";
	private static final String TARGETCLASSNAME2 = "moze_intel.projecte.gameObjs.tiles.DMFurnaceTile";
	private static final String TARGETCLASSNAME3 = "moze_intel.projecte.gameObjs.tiles.CondenserTile";


	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		//if (!FMLLaunchHandler.side().isClient()) {return basicClass;}
		if(!TARGETCLASSNAME.equals(transformedName) && !TARGETCLASSNAME2.equals(transformedName)
				&& !TARGETCLASSNAME3.equals(transformedName)) {return basicClass;}

		try {
			PEAACoreCorePlugin.logger.info("-------------------------Start PEAACore Transform--------------------------");
			ClassReader cr = new ClassReader(basicClass);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cr.accept(new CustomVisitor(name, cw, transformedName), 8);
			basicClass = cw.toByteArray();
			PEAACoreCorePlugin.logger.info("-------------------------Finish PEAACore Transform-------------------------");
		} catch (Exception e) {
			throw new RuntimeException("failed : PEAACoreTransformer loading", e);
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

		static final String targetMethodName = "<clinit>";				//ObjHandler
		static final String targetMethodName2 = "register";				//ObjHandler
		static final String targetMethodName3 = "<init>";				// DMFurnace TileEntity
		static final String targetMethodName4 = "getProgressScaled";	// CondenserTile

		/**
		 * ここでどのメソッドかを判断してそれぞれの書き換え処理に飛ばしている
		 */
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			// ObjHandlerのフィールド書き換え
			if (targetMethodName.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
					&& transformedName.equals(TARGETCLASSNAME)) {
				PEAACoreCorePlugin.logger.info("Start [clinit] Transform");
				return new CustomMethodVisitor(this.api, super.visitMethod(access, name, desc, signature, exceptions));

			}
			// ObjHandlerのregisterにTileEntityの追加
			if (targetMethodName2.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
					&& transformedName.equals(TARGETCLASSNAME)) {
				PEAACoreCorePlugin.logger.info("Add TileEntity [Mk2PEAA]");
				PEAACoreCorePlugin.logger.info("Add Entity [EntityWarterProjectilePEAA]");
				PEAACoreCorePlugin.logger.info("Add TileEntity [RMFurnaceTilePEAA]");
				return new CustomMethodVisitor2(this.api, super.visitMethod(access, name, desc, signature, exceptions));

			}
			// DMかまどの extends するTileEntityを変更する処理其の2
			if (targetMethodName3.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
					&& transformedName.equals(TARGETCLASSNAME2)) {
				PEAACoreCorePlugin.logger.info("Transform extends [RMFurnaceTilePEAA]");
				return new CustomMethodVisitor3(this.api, super.visitMethod(access, name, desc, signature, exceptions));

			}
			// コンデンサーのプログレスバー表示の修正
			if (targetMethodName4.equals(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, name, desc))
					&& transformedName.equals(TARGETCLASSNAME3)) {
				PEAACoreCorePlugin.logger.info("Transform extends [CondenserTile]");
				return new CustomMethodVisitor4(this.api, super.visitMethod(access, name, desc, signature, exceptions));

			}

			return super.visitMethod(access, name, desc, signature, exceptions);
		}

		String DMTileName = "moze_intel/projecte/gameObjs/tiles/DMFurnaceTile";
		/**
		 * DMかまどの extends するTileEntityを変更する処理其の1
		 */
		@Override
		public void visit(int version, int access, String name, String signature,
	            String superName, String[] interfaces) {
			if (name.equals(DMTileName)) {
				super.visit(version, access, name, signature, "peaa/gameObjs/tiles/RMFurnaceTilePEAA", interfaces);
				return;
			}

			super.visit(version, access, name, signature, superName, interfaces);
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
	 *
	 */
	class CustomMethodVisitor extends MethodVisitor {
		static final String targetFieldName = "condenserMk2";
		static final String targetFieldName2 = "rmFurnaceOff";
		static final String targetFieldName3 = "rmFurnaceOn";
		static final String targetFieldName4 = "everTide";

		public CustomMethodVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

		@Override
		public void visitFieldInsn(int opcode, String owner, String name, String desc) {
			if (targetFieldName.equals(name)) {
				PEAACoreCorePlugin.logger.info("Start [condenserMk2] Transform");
				mv.visitTypeInsn(NEW, "peaa/gameObjs/blocks/CondenserMK2PEAA");
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESPECIAL, "peaa/gameObjs/blocks/CondenserMK2PEAA", "<init>", "()V", false);

			}
			if (targetFieldName2.equals(name)) {
				PEAACoreCorePlugin.logger.info("Start [MatterFurnace_Off] Transform");
				mv.visitTypeInsn(NEW, "peaa/gameObjs/blocks/MatterFurnacePEAA");
				mv.visitInsn(DUP);
				mv.visitInsn(ICONST_0);
				mv.visitInsn(ICONST_1);
				mv.visitMethodInsn(INVOKESPECIAL, "peaa/gameObjs/blocks/MatterFurnacePEAA", "<init>", "(ZZ)V", false);

			}
			// RMTilePEAAに変更
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

			super.visitFieldInsn(opcode, owner, name, desc);
		}
	}

	/**
	 * [register]
	 * ObjHandlerに
	 * 		GameRegistry.registerTileEntity(CondenserMK2TilePEAA.class, "CondenserMK2 Tile  PEAA");
	 * を追加
	 *
	 * 変更PEAAにする
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

	/**
	 * mv.visitMethodInsn(INVOKESPECIAL, "moze_intel/projecte/gameObjs/tiles/RMFurnaceTile", "<init>", "()V", false);
	 * マターかまどの変更処理
	 */
	class CustomMethodVisitor3 extends MethodVisitor {
		static final String targetVisitMethodInsnName = "moze_intel/projecte/gameObjs/tiles/RMFurnaceTile";

		public CustomMethodVisitor3(int api, MethodVisitor mv) {
            super(api, mv);
        }

		@Override
		public void visitMethodInsn(int opcode, String owner, String name,
	            String desc, boolean itf) {
			if (owner.equals(targetVisitMethodInsnName)) {
				super.visitMethodInsn(opcode, "peaa/gameObjs/tiles/RMFurnaceTilePEAA", name, desc, itf);
				return;
			}
			super.visitMethodInsn(opcode, owner, name, desc, itf);

		}
	}

	/**
	 * コンデンサーのプログレスバーの表示が上手くいかない問題の修正
	 * とはいってもLongのキャストを挟むだけ(数が22億くらいを超えてintの上限を突破しているのが原因なので)
	 *
	 * return (displayEmc * Constants.MAX_CONDENSER_PROGRESS) / requiredEmc;
	 * から
	 * return (int)( ((long)displayEmc * Constants.MAX_CONDENSER_PROGRESS) / requiredEmc);
	 * にするだけ
	 */
	class CustomMethodVisitor4 extends MethodVisitor {
		static final String targetVisitMethodInsnName = "moze_intel/projecte/gameObjs/tiles/RMFurnaceTile";
		int count = 0;

		public CustomMethodVisitor4(int api, MethodVisitor mv) {
            super(api, mv);
        }

		/**
		 * 書き換えたい visitInsnは3つめ
		 */
		public void visitInsn(int opcode) {
			if (opcode == IRETURN)
				count++;

			if (count == 3) {
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
