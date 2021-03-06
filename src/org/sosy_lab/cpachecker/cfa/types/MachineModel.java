/*
 * IntPTI: integer error fixing by proper-type inference
 * Copyright (c) 2017.
 *
 * Open-source component:
 *
 * CPAchecker
 * Copyright (C) 2007-2014  Dirk Beyer
 *
 * Guava: Google Core Libraries for Java
 * Copyright (C) 2010-2006  Google
 *
 *
 */
package org.sosy_lab.cpachecker.cfa.types;

import static com.google.common.base.Preconditions.checkArgument;

import org.sosy_lab.cpachecker.cfa.ast.c.CExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CIntegerLiteralExpression;
import org.sosy_lab.cpachecker.cfa.types.c.CArrayType;
import org.sosy_lab.cpachecker.cfa.types.c.CBasicType;
import org.sosy_lab.cpachecker.cfa.types.c.CComplexType;
import org.sosy_lab.cpachecker.cfa.types.c.CCompositeType;
import org.sosy_lab.cpachecker.cfa.types.c.CCompositeType.CCompositeTypeMemberDeclaration;
import org.sosy_lab.cpachecker.cfa.types.c.CElaboratedType;
import org.sosy_lab.cpachecker.cfa.types.c.CEnumType;
import org.sosy_lab.cpachecker.cfa.types.c.CFunctionType;
import org.sosy_lab.cpachecker.cfa.types.c.CNumericTypes;
import org.sosy_lab.cpachecker.cfa.types.c.CPointerType;
import org.sosy_lab.cpachecker.cfa.types.c.CProblemType;
import org.sosy_lab.cpachecker.cfa.types.c.CSimpleType;
import org.sosy_lab.cpachecker.cfa.types.c.CType;
import org.sosy_lab.cpachecker.cfa.types.c.CTypeVisitor;
import org.sosy_lab.cpachecker.cfa.types.c.CTypedefType;
import org.sosy_lab.cpachecker.cfa.types.c.CVoidType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import javax.annotation.Nullable;

/**
 * This enum stores the sizes for all the basic types that exist.
 */
public enum MachineModel {
  /**
   * Machine model representing a 32bit Linux machine with alignment:
   */
  LINUX32(
      // numeric types
      2,  // short
      4,  // int
      4,  // long int
      8,  // long long int
      4,  // float
      8,  // double
      12, // long double

      // other
      1, // void
      1, // bool
      4, // pointer

      // alignof numeric types
      2, // short
      4, //int
      4, //long int
      8, // long long int
      4, //float
      4, //double
      4, //long double

      // alignof other
      1, // void
      1, //bool
      4, //pointer

      // definition of float
      8,
      23,

      // definition of double
      8,
      23,

      // definition of long double
      8,
      23
  ),

  /**
   * Machine model representing a 64bit Linux machine with alignment:
   */
  LINUX64(
      // numeric types
      2,  // short
      4,  // int
      8,  // long int
      8,  // long long int
      4,  // float
      8,  // double
      16, // long double

      // other
      1, // void
      1, // bool
      8, // pointer

      //  alignof numeric types
      2,  // short
      4,  // int
      8,  // long int
      8,  // long long int
      4,  // float
      8,  // double
      16, // long double

      // alignof other
      1, // void
      1, // bool
      8, // pointer

      // definition of float
      8,
      23,

      // definition of double
      11,
      52,

      // definition of long double
      15,
      63
  );

  // numeric types
  private final int sizeofShort;
  private final int sizeofInt;
  private final int sizeofLongInt;
  private final int sizeofLongLongInt;
  private final int sizeofFloat;
  private final int sizeofDouble;
  private final int sizeofLongDouble;

  // other
  private final int sizeofVoid;
  private final int sizeofBool;
  private final int sizeofPtr;

  // alignof numeric types
  private final int alignofShort;
  private final int alignofInt;
  private final int alignofLongInt;
  private final int alignofLongLongInt;
  private final int alignofFloat;
  private final int alignofDouble;
  private final int alignofLongDouble;

  // alignof other
  private final int alignofVoid;
  private final int alignofBool;
  private final int alignofPtr;

  // definition of float
  private final int sizeofExponentFloat;
  private final int sizeofFractionFloat;

  // definition of double
  private final int sizeofExponentDouble;
  private final int sizeofFractionDouble;

  // definition of long double
  private final int sizeofExponentLongDouble;
  private final int sizeofFractionLongDouble;

  // according to ANSI C, sizeof(char) is always 1
  private final int mSizeofChar = 1;
  private final int mAlignofChar = 1;

  // a char is always a byte, but a byte doesn't have to be 8 bits
  private final int mSizeofCharInBits = 8;
  private final CSimpleType ptrEquivalent;

  private final CSimpleType size_t;

  MachineModel(
      int pSizeofShort,
      int pSizeofInt,
      int pSizeofLongInt,
      int pSizeofLongLongInt,
      int pSizeofFloat,
      int pSizeofDouble,
      int pSizeofLongDouble,
      int pSizeofVoid,
      int pSizeofBool,
      int pSizeOfPtr,
      int pAlignofShort,
      int pAlignofInt,
      int pAlignofLongInt,
      int pAlignofLongLongInt,
      int pAlignofFloat,
      int pAlignofDouble,
      int pAlignofLongDouble,
      int pAlignofVoid,
      int pAlignofBool,
      int pAlignofPtr,
      int pSizeofExponentFloat,
      int pSizeofFractionFloat,
      int pSizeofExponentDouble,
      int pSizeofFractionDouble,
      int pSizeofExponentLongDouble,
      int pSizeofFractionLongDouble) {
    sizeofShort = pSizeofShort;
    sizeofInt = pSizeofInt;
    sizeofLongInt = pSizeofLongInt;
    sizeofLongLongInt = pSizeofLongLongInt;
    sizeofFloat = pSizeofFloat;
    sizeofDouble = pSizeofDouble;
    sizeofLongDouble = pSizeofLongDouble;
    sizeofVoid = pSizeofVoid;
    sizeofBool = pSizeofBool;
    sizeofPtr = pSizeOfPtr;

    alignofShort = pAlignofShort;
    alignofInt = pAlignofInt;
    alignofLongInt = pAlignofLongInt;
    alignofLongLongInt = pAlignofLongLongInt;
    alignofFloat = pAlignofFloat;
    alignofDouble = pAlignofDouble;
    alignofLongDouble = pAlignofLongDouble;
    alignofVoid = pAlignofVoid;
    alignofBool = pAlignofBool;
    alignofPtr = pAlignofPtr;

    sizeofExponentFloat = pSizeofExponentFloat;
    sizeofFractionFloat = pSizeofFractionFloat;
    sizeofExponentDouble = pSizeofExponentDouble;
    sizeofFractionDouble = pSizeofFractionDouble;
    sizeofExponentLongDouble = pSizeofExponentLongDouble;
    sizeofFractionLongDouble = pSizeofFractionLongDouble;

    if (sizeofPtr == sizeofInt) {
      ptrEquivalent = CNumericTypes.INT;
      size_t = CNumericTypes.UNSIGNED_INT;
    } else if (sizeofPtr == sizeofLongInt) {
      ptrEquivalent = CNumericTypes.LONG_INT;
      size_t = CNumericTypes.UNSIGNED_LONG_INT;
    } else if (sizeofPtr == sizeofLongLongInt) {
      ptrEquivalent = CNumericTypes.LONG_LONG_INT;
      size_t = CNumericTypes.UNSIGNED_LONG_LONG_INT;
    } else if (sizeofPtr == sizeofShort) {
      ptrEquivalent = CNumericTypes.SHORT_INT;
      size_t = CNumericTypes.UNSIGNED_SHORT_INT;
    } else {
      throw new AssertionError("No ptr-Equivalent found");
    }
  }

  public CSimpleType getPointerEquivalentSimpleType() {
    return ptrEquivalent;
  }

  /**
   * This method returns the signed integer type of the result
   * of subtracting two pointers, also called <code>ptrdiff_t</code>.
   *
   * <p>From ISO-C99 (6.5.6, #9):<p>
   * When two pointers are subtracted, [...] The size of the result is implementation-defined,
   * and its type (a signed integer type) is <code>ptrdiff_t</code> defined in the stddef.h-header.
   */
  public CSimpleType getPointerDiffType() {
    // ptrEquivalent should not be unsigned, so canonical type is always signed
    assert !ptrEquivalent.isUnsigned();
    return ptrEquivalent.getCanonicalType();
  }

  public CSimpleType getSizeTType() {
    assert size_t.isUnsigned();
    return size_t;
  }

  /**
   * This method decides, if a plain <code>char</code> is signed or unsigned.
   *
   * <p>From ISO-C99 (6.2.5, #15):<p> The three types <code>char</code>, <code>signed char</code>,
   * and <code>unsigned char</code> are collectively called the <i>character types</i>. The
   * implementation shall define <code>char</code> to have the same range, representation, and
   * behavior as either <code>signed char</code> or <code>unsigned char</code>.
   */
  public boolean isDefaultCharSigned() {
    return true;
  }

  /**
   * Determine whether a type is signed or unsigned.
   * Contrary to {@link CSimpleType#isSigned()} and {@link CSimpleType#isUnsigned()}
   * this method leaves no third option and should thus be preferred.
   * For floating point types it returns true,
   * for types where signedness makes no sense (bool, void) it returns false.
   */
  public boolean isSigned(CSimpleType t) {
    // resolve UNSPECIFIED and INT to SIGNED INT etc.
    t = t.getCanonicalType();

    if (t.isSigned()) {
      return true;
    } else if (t.isUnsigned()) {
      return false;
    }

    switch (t.getType()) {
      case CHAR:
        return isDefaultCharSigned();
      case FLOAT:
      case DOUBLE:
        return true;
      case INT:
        throw new AssertionError("Canonical type of INT should always have sign modifier");
      case UNSPECIFIED:
        throw new AssertionError("Canonical type should never be UNSPECIFIED");
      default:
        // bool, void
        return false;
    }
  }

  public int getSizeofCharInBits() {
    return mSizeofCharInBits;
  }

  public int getSizeofShort() {
    return sizeofShort;
  }

  public int getSizeofInt() {
    return sizeofInt;
  }

  public int getSizeofLongInt() {
    return sizeofLongInt;
  }

  public int getSizeofLongLongInt() {
    return sizeofLongLongInt;
  }

  public int getSizeofFloat() {
    return sizeofFloat;
  }

  public int getSizeofDouble() {
    return sizeofDouble;
  }

  public int getSizeofLongDouble() {
    return sizeofLongDouble;
  }

  public int getSizeofVoid() {
    return sizeofVoid;
  }

  public int getSizeofBool() {
    return sizeofBool;
  }

  public int getSizeofChar() {
    return mSizeofChar;
  }

  public int getSizeofPtr() {
    return sizeofPtr;
  }

  public int getSizeof(CSimpleType type) {
    switch (type.getType()) {
      case BOOL:
        return getSizeofBool();
      case CHAR:
        return getSizeofChar();
      case FLOAT:
        return getSizeofFloat();
      case UNSPECIFIED: // unspecified is the same as int
      case INT:
        if (type.isLongLong()) {
          return getSizeofLongLongInt();
        } else if (type.isLong()) {
          return getSizeofLongInt();
        } else if (type.isShort()) {
          return getSizeofShort();
        } else {
          return getSizeofInt();
        }
      case DOUBLE:
        if (type.isLong()) {
          return getSizeofLongDouble();
        } else {
          return getSizeofDouble();
        }
      default:
        throw new AssertionError("Unrecognized CBasicType " + type.getType());
    }
  }

  public int getSizeofInBits(CSimpleType type) {
    return getSizeof(type) * getSizeofCharInBits();
  }

  public int getAlignofShort() {
    return alignofShort;
  }

  public int getAlignofInt() {
    return alignofInt;
  }

  public int getAlignofLongInt() {
    return alignofLongInt;
  }

  public int getAlignofLongLongInt() {
    return alignofLongLongInt;
  }

  public int getAlignofFloat() {
    return alignofFloat;
  }

  public int getAlignofDouble() {
    return alignofDouble;
  }

  public int getAlignofLongDouble() {
    return alignofLongDouble;
  }

  public int getAlignofVoid() {
    return alignofVoid;
  }

  public int getAlignofBool() {
    return alignofBool;
  }

  public int getAlignofChar() {
    return mAlignofChar;
  }

  public int getAlignofPtr() {
    return alignofPtr;
  }

  public int getAlignof(CSimpleType type) {
    switch (type.getType()) {
      case BOOL:
        return getAlignofBool();
      case CHAR:
        return getAlignofChar();
      case FLOAT:
        return getAlignofFloat();
      case UNSPECIFIED: // unspecified is the same as int
      case INT:
        if (type.isLongLong()) {
          return getAlignofLongLongInt();
        } else if (type.isLong()) {
          return getAlignofLongInt();
        } else if (type.isShort()) {
          return getAlignofShort();
        } else {
          return getAlignofInt();
        }
      case DOUBLE:
        if (type.isLong()) {
          return getAlignofLongDouble();
        } else {
          return getAlignofDouble();
        }
      default:
        throw new AssertionError("Unrecognized CBasicType " + type.getType());
    }
  }

  /**
   * returns INT, if the type is smaller than INT, else the type itself.
   */
  public CSimpleType getPromotedCType(CSimpleType pType) {

    /*
     * ISO-C99 (6.3.1.1 #2):
     * If an int can represent all values of the original type, the value is
     * converted to an int; otherwise, it is converted to an unsigned int.
     * These are called the integer promotions.
     */
    // TODO when do we really need unsigned_int?
    if (getSizeof(pType) < getSizeofInt()) {
      return CNumericTypes.SIGNED_INT;
    } else {
      return pType;
    }
  }

  public boolean needPromotion(CSimpleType pType) {
    return getSizeof(pType) < getSizeofInt();
  }

  /**
   * Get the minimal representable value for an integer type.
   *
   * @throws IllegalArgumentException If the type is not an integer type as defined by {@link
   *                                  CBasicType#isIntegerType()}.
   */
  public BigInteger getMinimalIntegerValue(CSimpleType pType) {
    if (pType.getType().isIntegerType()) {
      if (isSigned(pType)) {
        return twoToThePowerOf(getSizeofInBits(pType) - 1).negate();
      } else {
        return BigInteger.ZERO;
      }
    } else {
      BigDecimal result = getMaximalIntegerValueForFloating(pType);
      result = result.negate();
      result = result.setScale(0, RoundingMode.FLOOR);
      return result.toBigInteger();
    }
  }

  private BigDecimal getMaximalIntegerValueForFloating(CSimpleType pType) {
    checkArgument(pType.getType().isFloatingPointType());
    int sizeOfExponent, sizeOfFraction;
    switch (pType.getType()) {
      case FLOAT:
        sizeOfExponent = sizeofExponentFloat;
        sizeOfFraction = sizeofFractionFloat;
        break;
      case DOUBLE:
        if (pType.isLong()) {
          sizeOfExponent = sizeofExponentLongDouble;
          sizeOfFraction = sizeofFractionLongDouble;
        } else {
          sizeOfExponent = sizeofExponentDouble;
          sizeOfFraction = sizeofFractionDouble;
        }
        break;
      default:
        throw new AssertionError("Unrecognized float type: " + pType.getType());
    }
    int exp1 = (int) Math.pow(2, sizeOfExponent - 1);
    int exp2 = exp1 - sizeOfFraction - 1;
    BigDecimal decimal1 = new BigDecimal(2);
    BigDecimal decimal2 = new BigDecimal(2);
    decimal1 = decimal1.pow(exp1);
    decimal2 = decimal2.pow(exp2);
    return decimal1.subtract(decimal2);
  }

  /**
   * Get the maximal representable value for an integer type.
   *
   * @throws IllegalArgumentException If the type is not an integer type as defined by {@link
   *                                  CBasicType#isIntegerType()}.
   */
  public BigInteger getMaximalIntegerValue(CSimpleType pType) {
    if (pType.getType().isIntegerType()) {
      if (pType.getType() == CBasicType.BOOL) {
        return BigInteger.ONE;
      } else if (isSigned(pType)) {
        return twoToThePowerOf(getSizeofInBits(pType) - 1).subtract(BigInteger.ONE);
      } else {
        return twoToThePowerOf(getSizeofInBits(pType)).subtract(BigInteger.ONE);
      }
    } else {
      BigDecimal result = getMaximalIntegerValueForFloating(pType);
      result = result.setScale(0, RoundingMode.CEILING);
      return result.toBigInteger();
    }
  }

  private static BigInteger twoToThePowerOf(int exp) {
    assert exp > 0 : "Exponent " + exp + " is not greater than zero.";
    BigInteger result = BigInteger.ZERO.setBit(exp);
    assert BigInteger.valueOf(2).pow(exp).equals(result);
    return result;
  }

  private final CTypeVisitor<Integer, IllegalArgumentException> sizeofVisitor =
      new BaseSizeofVisitor(this);

  public static class BaseSizeofVisitor implements CTypeVisitor<Integer, IllegalArgumentException> {
    protected final MachineModel model;

    public BaseSizeofVisitor(MachineModel model) {
      this.model = model;
    }

    @Override
    public Integer visit(CArrayType pArrayType) throws IllegalArgumentException {
      // TODO: Take possible padding into account

      CExpression arrayLength = pArrayType.getLength();

      if (arrayLength instanceof CIntegerLiteralExpression) {
        int length = ((CIntegerLiteralExpression) arrayLength).getValue().intValue();

        int sizeOfType = model.getSizeof(pArrayType.getType());
        return length * sizeOfType;
      }

      // Treat arrays with variable length as pointer.
      return model.getSizeofPtr();
    }

    @Override
    public Integer visit(CCompositeType pCompositeType) throws IllegalArgumentException {

      switch (pCompositeType.getKind()) {
        case STRUCT:
          return handleSizeOfStruct(pCompositeType);
        case UNION:
          return handleSizeOfUnion(pCompositeType);
        case ENUM: // There is no such kind of Composite Type.
        default:
          throw new AssertionError();
      }
    }

    private Integer handleSizeOfStruct(CCompositeType pCompositeType) {
      int size = 0;
      for (CCompositeTypeMemberDeclaration decl : pCompositeType.getMembers()) {
        size += model.getPadding(size, decl.getType());
        size += decl.getType().accept(this);
      }
      size += model.getPadding(size, pCompositeType);
      return size;
    }

    private Integer handleSizeOfUnion(CCompositeType pCompositeType) {
      int size = 0;
      int sizeOfType;
      for (CCompositeTypeMemberDeclaration decl : pCompositeType.getMembers()) {
        sizeOfType = decl.getType().accept(this);
        size = Math.max(size, sizeOfType);
      }
      size += model.getPadding(size, pCompositeType);
      return size;
    }

    @Override
    public Integer visit(CElaboratedType pElaboratedType) throws IllegalArgumentException {
      CType def = pElaboratedType.getRealType();
      if (def != null) {
        return def.accept(this);
      }

      switch (pElaboratedType.getKind()) {
        case ENUM:
          return model.getSizeofInt();
        case STRUCT:
          // TODO: UNDEFINED
          return model.getSizeofInt();
        case UNION:
          // TODO: UNDEFINED
          return model.getSizeofInt();
        default:
          return model.getSizeofInt();
      }
    }

    @Override
    public Integer visit(CEnumType pEnumType) throws IllegalArgumentException {
      return model.getSizeofInt();
    }

    @Override
    public Integer visit(CFunctionType pFunctionType) throws IllegalArgumentException {
      // A function does not really have a size,
      // but references to functions can be used as pointers.
      return model.getSizeofPtr();
    }

    @Override
    public Integer visit(CPointerType pPointerType) throws IllegalArgumentException {
      return model.getSizeofPtr();
    }

    @Override
    public Integer visit(CProblemType pProblemType) throws IllegalArgumentException {
      throw new IllegalArgumentException("Unknown C-Type: " + pProblemType.getClass().toString());
    }

    @Override
    public Integer visit(CSimpleType pSimpleType) throws IllegalArgumentException {
      return model.getSizeof(pSimpleType);
    }

    @Override
    public Integer visit(CTypedefType pTypedefType) throws IllegalArgumentException {
      return pTypedefType.getRealType().accept(this);
    }

    @Override
    public Integer visit(CVoidType pVoidType) throws IllegalArgumentException {
      return model.getSizeofVoid();
    }
  }

  public int getSizeof(CType type) {
    return type.accept(sizeofVisitor);
  }

  private final CTypeVisitor<Integer, IllegalArgumentException> alignofVisitor =
      new BaseAlignofVisitor(this);

  public static class BaseAlignofVisitor
      implements CTypeVisitor<Integer, IllegalArgumentException> {
    private final MachineModel model;

    public BaseAlignofVisitor(MachineModel model) {
      this.model = model;
    }

    @Override
    public Integer visit(CArrayType pArrayType) throws IllegalArgumentException {
      // the alignment of an array is the same as the alignment of an member of the array
      return pArrayType.getType().accept(this);
    }

    @Override
    public Integer visit(CCompositeType pCompositeType) throws IllegalArgumentException {

      switch (pCompositeType.getKind()) {
        case STRUCT:
        case UNION:
          int alignof = 1;
          int alignOfType = 0;
          // TODO: Take possible padding into account
          for (CCompositeTypeMemberDeclaration decl : pCompositeType.getMembers()) {
            alignOfType = decl.getType().accept(this);
            alignof = Math.max(alignof, alignOfType);
          }
          return alignof;

        case ENUM: // There is no such kind of Composite Type.
        default:
          throw new AssertionError();
      }
    }

    @Override
    public Integer visit(CElaboratedType pElaboratedType) throws IllegalArgumentException {
      CComplexType realType = pElaboratedType.getRealType();
      if (realType == null) {
        // or equivalent to the alignment of char, I suppose
        return 1;
      }
      return pElaboratedType.getRealType().accept(this);
    }

    @Override
    public Integer visit(CEnumType pEnumType) throws IllegalArgumentException {
      // enums are always ints
      return model.getAlignofInt();
    }

    @Override
    public Integer visit(CFunctionType pFunctionType) throws IllegalArgumentException {
      // function types have per definition the value 1 if compiled with gcc
      return 1;
    }

    @Override
    public Integer visit(CPointerType pPointerType) throws IllegalArgumentException {
      return model.getAlignofPtr();
    }

    @Override
    public Integer visit(CProblemType pProblemType) throws IllegalArgumentException {
      throw new IllegalArgumentException("Unknown C-Type: " + pProblemType.getClass().toString());
    }

    @Override
    public Integer visit(CSimpleType pSimpleType) throws IllegalArgumentException {
      return model.getAlignof(pSimpleType);
    }

    @Override
    public Integer visit(CTypedefType pTypedefType) throws IllegalArgumentException {
      return pTypedefType.getRealType().accept(this);
    }

    @Override
    public Integer visit(CVoidType pVoidType) throws IllegalArgumentException {
      return model.getAlignofVoid();
    }
  }

  public int getAlignof(CType type) {
    return type.accept(alignofVisitor);
  }

  public int getPadding(int pOffset, CType pType) {
    int alignof = getAlignof(pType);
    int padding = alignof - (pOffset % alignof);
    if (padding < alignof) {
      return padding;
    }
    return 0;
  }

  /**
   * This function returns a C type given
   * (1) whether this type is integer type or not;
   * (2) the signedness of this type (valid only for integer types)
   * (3) the size of type, which is given by the times as wide as the smallest addressable unit
   * (i.e. 1 byte = 8 bits)
   *
   * @param isInteger (1)
   * @param isSigned  (2)
   * @param times     (3)
   * @return the desired C type or null if such type does not exist
   */
  @Nullable
  public CType getType(boolean isInteger, boolean isSigned, int times) {
    if (isInteger) {
      if (times == mSizeofChar) {
        return isSigned ? CNumericTypes.SIGNED_CHAR : CNumericTypes.UNSIGNED_CHAR;
      } else if (times == sizeofShort) {
        return isSigned ? CNumericTypes.SHORT_INT : CNumericTypes.UNSIGNED_SHORT_INT;
      } else if (times == sizeofInt) {
        return isSigned ? CNumericTypes.INT : CNumericTypes.UNSIGNED_INT;
      } else if (times == sizeofLongInt) {
        return isSigned ? CNumericTypes.LONG_INT : CNumericTypes.UNSIGNED_LONG_INT;
      } else if (times == sizeofLongLongInt) {
        return isSigned ? CNumericTypes.LONG_LONG_INT : CNumericTypes.UNSIGNED_LONG_LONG_INT;
      }
    } else {
      if (times == sizeofFloat) {
        return CNumericTypes.FLOAT;
      } else if (times == sizeofDouble) {
        return CNumericTypes.DOUBLE;
      } else if (times == sizeofLongDouble) {
        return CNumericTypes.LONG_DOUBLE;
      }
    }
    return null;
  }
}
