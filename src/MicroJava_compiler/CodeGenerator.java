package rs.ac.bg.etf.pp1;

import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;

public class CodeGenerator extends VisitorAdaptor {
	private Logger log = Logger.getLogger(getClass());				
	private Obj globl = null;
	
	private enum AddOp 	      {opAdd, opSub};
	private enum MulOp 		  {opMul, opDiv, opMod};
	private enum AssignAddSub { Assign, AddAssign, SubAssign, MulAssign, DivAssign, ModAssign };
	
	private LinkedList<AddOp>        mAddOpStack 	  = new LinkedList<CodeGenerator.AddOp>();
	private LinkedList<MulOp>        mMulOpStack      = new LinkedList<CodeGenerator.MulOp>();	
	private LinkedList<AssignAddSub> mAssignOperators = new LinkedList<CodeGenerator.AssignAddSub>();
	private LinkedList<Obj>          mAssignOperands  = new LinkedList<Obj>();
	
	private int mCurrRelOp = -1;		// ne koristi se - obrisati
	
	private boolean needPop = false;

	private boolean errorDetected = false;
	private Obj mCurrMethod = null;	

	private ArrayList<Var> myVars = new ArrayList<Var>();	//first retrives names divided by ',' and then type at the end
	private Var mVar = null;
	
	//----------------------------------------------------------------------------------------------------------------
	public CodeGenerator(Obj globl) {
		super();
		this.globl = globl;
	}
	
	//----------------------------------------------------------------------------------------------------------------
	// getting Method
	public Obj getMethObj(String methName) {
		Obj obj = Tab.find(methName);
		if (obj == Tab.noObj) {
			obj = null;
			Collection<Obj> coll = globl.getLocalSymbols();
			Iterator<Obj> i = coll.iterator();			
			while (i.hasNext()) {
				obj = i.next();
				if (obj.getKind() == Obj.Meth && obj.getName().equals(methName)) {
					return obj;
				}
			}
		} else {
			return obj;
		}
		return null;
	}
		
	// getting Object if current method is known
	public Obj getNonMethObj(String objName) {
		Obj obj = Tab.find(objName);
		if (obj == Tab.noObj) {
			obj = null;
			Collection<Obj> coll = globl.getLocalSymbols();
			Iterator<Obj> i = coll.iterator();			
			while (i.hasNext()) {
				obj = i.next();
				if (obj.getKind() != Obj.Meth && obj.getName().equals(objName)) {
					return obj;
				}
			}
			
			if (mCurrMethod != null) {
				coll = mCurrMethod.getLocalSymbols();
				i = coll.iterator();		
				while (i.hasNext()) {
					obj = i.next();
					if (obj.getName().equals(objName)) {
						return obj;
					}
				}
			}		
		} else {
			return obj;
		}
		return null;
	}
	//----------------------------------------------------------------------------------------------------------------
	
	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0) {
			msg.append(" na liniji ").append(line).append("!");
		}
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0) {
			msg.append(" na liniji ").append(line);
		}
		log.info(msg.toString());
	}
	
	//----------------------------------------------------------------------------------------------------------------
	public boolean isErrorDetected() {
		return errorDetected;
	}	
	
	//----------------------------------------------------------------------------------------------------------------
	@Override	    			
	public void visit(Program_0 Program_0) {		
		Code.dataSize = globl.getLocalSymbols().size();
		errorDetected = errorDetected || Code.greska;		
	}
	
	@Override	    			
	public void visit(Program_1 Program_1) {		
		Code.dataSize = globl.getLocalSymbols().size();
		errorDetected = errorDetected || Code.greska;		
	}
	
	@Override	    			
	public void visit(Program_2 Program_2) {		
		Code.dataSize = globl.getLocalSymbols().size();
		errorDetected = errorDetected || Code.greska;		
	}
	
	@Override	    			
	public void visit(Program_3 Program_3) {		
		Code.dataSize = globl.getLocalSymbols().size();
		errorDetected = errorDetected || Code.greska;		
	}	

	@Override
	public void visit(Method_type_name Method_type_name) {		
		mCurrMethod = getMethObj(Method_type_name.getMethodName());
		if (mCurrMethod.getName().equals("main")) {
			Code.mainPc = Code.pc;			
		}		
        mCurrMethod.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(mCurrMethod.getLevel());
        Code.put(mCurrMethod.getLocalSymbols().size());       
	}
	
	@Override
    public void visit(Meth_type_decl meth_type_decl) {
		Code.put(Code.trap); 
		Code.put(1);
		mCurrMethod = null;
	}
	
	@Override
	public void visit(Method_void_name Method_void_name) { 		
		mCurrMethod = getMethObj(Method_void_name.getMethodName());			
		if (mCurrMethod.getName().equals("main")) {
			Code.mainPc = Code.pc;			
		}
        mCurrMethod.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(mCurrMethod.getLevel());
        Code.put(mCurrMethod.getLocalSymbols().size());       
	}
	
	@Override
    public void visit(Meth_void_decl meth_void_decl) {
		Code.put(Code.exit); 
		Code.put(Code.return_);
		mCurrMethod = null;
	}

	@Override
	public void visit(Const_declaration Const_declaration) {		
		for (int i = 0; i < myVars.size(); i++) {			
			Obj o = getNonMethObj(myVars.get(i).getName());			
			if (o != null) {
				Object val = myVars.get(i).getValue();
				if (val instanceof Integer) {
					o.setAdr((Integer) val);
				} else if (val instanceof Character) {
					o.setAdr((Character) val);
				} else if (val instanceof Boolean) {				
					o.setAdr((Boolean) val ? 1 : 0);
				} else {
					o.setAdr(0);
				}
				Code.load(o);
			}
		}
		myVars.clear();
	}	
	
	// set names for constants
	@Override
	public void visit(Const_part Const_part) {
		mVar.setName(Const_part.getConstName());
		myVars.add(mVar);
		mVar = null;
	}
	
	@Override
    public void visit(Type Type) {
    	// ...
    }

	@Override
	public void visit(Val_Num_const Val_Num_const) { 
		mVar = new Var(Val_Num_const.getN1());
	}
	
	@Override
	public void visit(Val_Char_const Val_Char_const) { 
		mVar = new Var(Val_Char_const.getC1());
	}    
	
	@Override
	public void visit(Val_Bool_const Val_Bool_const) { 
		mVar = new Var(Val_Bool_const.getB1());
	}			    	
	
	@Override
    public void visit(Var_declaration Var_declaration) {}
	
	@Override
    public void visit(Var_error Var_error) { visit(); }
	
	@Override
	public void visit(Factor_num_const Factor_num_const) {				
		Code.loadConst(Factor_num_const.getN1());				
	}
	
	@Override
	public void visit(Factor_char_const Factor_char_const) {		
		Code.loadConst(Factor_char_const.getC1());			
	}
	
	@Override
	public void visit(Factor_expr Factor_expr) {}
	
	
	@Override
	public void visit(Factor_bool_const Factor_bool_const) {				
		int value = Factor_bool_const.getB1() ? 1 : 0;
		Code.loadConst(value);		
	}
	
	@Override
	public void visit(Factor_new_array Factor_new_array) {		
		Code.put(Code.newarray);
        if (Factor_new_array.getType().struct == Tab.charType) {
			Code.put(0); 
        } else { 
			Code.put(1);
        }        
	}
	
	@Override
	public void visit(Factor_designator Factor_designator) {
		Obj o = getNonMethObj(Factor_designator.getDesignator().obj.getName());
		if (o != null) {
			Code.load(Factor_designator.getDesignator().obj);
			if(Factor_designator.getDesignator().obj.getKind() == Obj.Elem)
			{
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.put(Code.pop);
				Code.put(Code.pop);
				needPop = false;
			}
		}
	}
	
	@Override
	public void visit(Factor_meth_call_0 Factor_meth_call) {
		Obj method = Factor_meth_call.getDesignator().obj;
		if (globl.getLocalSymbols().contains(method)) {
			int offset = method.getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(offset);
		}
	}
	
	@Override
	public void visit(Factor_meth_call_1 Factor_meth_call) {
		Obj method = Factor_meth_call.getDesignator().obj;
		if (globl.getLocalSymbols().contains(method)) {
			int offset = method.getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(offset);
		}
	}
	
	@Override
	public void visit(Method_call_0 Method_call_0) {
		Obj method = Method_call_0.getDesignator().obj;
		if (globl.getLocalSymbols().contains(method)) {
			int offset = method.getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(offset);
		}
	}
	
	@Override
	public void visit(Method_call_1 Method_call_1) { 
		Obj method = Method_call_1.getDesignator().obj;
		if (globl.getLocalSymbols().contains(method)) {
			int offset = method.getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(offset);
		}
	}
	
	
	@Override
	public void visit(Stmt_design Stmt_design) {}
	
	
	@Override
	public void visit(Stmt_Read Stmt_Read) {		
		Code.put(Code.read);
		Code.store(Stmt_Read.getDesignator().obj);		
		if(Stmt_Read.getDesignator().obj.getKind() == Obj.Elem)
		{
			Code.put(Code.pop);
			Code.put(Code.pop);
		}
	}		
	
	@Override
	public void visit(Stmt_print_0 Stmt_print_0) {		
		Struct struct = Stmt_print_0.getExpr().struct;
		if (struct.equals(Tab.intType) || struct.equals(SemanticAnalyzer.getBoolType())) {
			Code.loadConst(Stmt_print_0.getN2());
			Code.put(Code.print);
		} else if (struct.equals(Tab.charType)) {
			Code.loadConst(1);
			Code.put(Code.bprint);
		}		
	}

	@Override
	public void visit(Stmt_print_1 Stmt_print_1) {		
		Struct struct = Stmt_print_1.getExpr().struct;
		if (struct.equals(Tab.intType) || struct.equals(SemanticAnalyzer.getBoolType())) {
			Code.loadConst(0);
			Code.put(Code.print);
		} else if (struct.equals(Tab.charType)) {
			Code.loadConst(1);
			Code.put(Code.bprint);
		}		
	}

	@Override
	public void visit(Stmt_return_val Stmt_return_val) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(Stmt_return Stmt_return) { 
		Code.put(Code.exit);
		Code.put(Code.return_);
	}	
	
	@Override
	public void visit(Increment Increment) {
		Code.load(Increment.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(Increment.getDesignator().obj);		
	}
	
	@Override
	public void visit(Decrement Decrement) {
		Code.load(Decrement.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(Decrement.getDesignator().obj);		
	}
	
	@Override
	public void visit(Designator_0 Designator_0) {		
		Obj o = null;
		o = getNonMethObj(Designator_0.getID());	
		if (o != null) {
			Code.load(o);
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			
			{
				Code.put(Code.dup2);
				needPop = true;
			}
		}				
	} 
	
	@Override
	public void visit(Designator_1 Designator_1) {}
	
	@Override
	public void visit(Term_multi Term_multi) {				
		SyntaxNode parent = (SyntaxNode) Term_multi.getParent();
		if (parent.getClass() == Expr_neg_term_multi.class 
				|| parent.getClass() == Expr_neg_term_single.class) {
			Code.put(Code.neg);
		}
	}
	
	@Override
	public void visit(Term_single Term_single) { 		
		SyntaxNode parent = (SyntaxNode) Term_single.getParent();
		if (parent.getClass() == Expr_neg_term_multi.class 
				|| parent.getClass() == Expr_neg_term_single.class) {
			Code.put(Code.neg);
		}
	} 
	
	@Override
	public void visit(Multiplication Mul_op_mul) {		
		mMulOpStack.addLast(MulOp.opMul);
	}
	@Override
	public void visit(Division Mul_op_div) {
		mMulOpStack.addLast(MulOp.opDiv);
	}
	
	@Override
	public void visit(Modulo Mul_op_mod) {
		mMulOpStack.addLast(MulOp.opMod);
	}

	@Override
	public void visit(Add_op_left_add Add_op_left_add) {
		mAddOpStack.addLast(AddOp.opAdd);
	}
	
	@Override
	public void visit(Add_op_left_sub Add_op_left_sub) {
		mAddOpStack.addLast(AddOp.opSub);
	}
	
	@Override
	public void visit(More_term_part More_term_part) {
		AddOp currAddOp = mAddOpStack.removeLast();
		if (currAddOp != null) {			
			switch (currAddOp) {
				case opAdd: 
					Code.put(Code.add);
					break;
				case opSub: 
					Code.put(Code.sub);
					break;
			}
		}
		currAddOp = null;
	}
	
	@Override
	public void visit(More_factor_part More_factor_part) {
		MulOp currMulOp = mMulOpStack.removeLast();
		if (currMulOp != null) {						
			switch (currMulOp) {
				case opMul: 
					Code.put(Code.mul);
					break;
				case opDiv:
					Code.put(Code.div);
					break;
				case opMod: 
					Code.put(Code.rem);
					break;
			}
		}		
		currMulOp = null;
	}
	
	@Override
	public void visit(Rel_op_gt Rel_op_gt) { 
		mCurrRelOp = Code.gt; 		
	}
	
	@Override
	public void visit(Rel_op_eq Rel_op_eq) { 
		mCurrRelOp = Code.eq;		
	}
	
	@Override
	public void visit(Rel_op_lt Rel_op_lt) { 
		mCurrRelOp = Code.lt;		
	}
	
	@Override
	public void visit(Rel_op_ge Rel_op_ge) { 
		mCurrRelOp = Code.ge;		
	}
	
	@Override
	public void visit(Rel_op_le Rel_op_le) { 
		mCurrRelOp = Code.le;		
	}
	
	@Override
    public void visit(Rel_op_ne Rel_op_ne) { 
    	mCurrRelOp = Code.ne;
    }
	
	
	public void visit(Add_And_Assign_Right Add_And_Assign_Right)
	{
		mAssignOperators.addLast(AssignAddSub.AddAssign);
	}
	
	public void visit(Sub_And_Assign_Right Sub_And_Assign_Right)
	{
		mAssignOperators.addLast(AssignAddSub.SubAssign);
	}
	
	public void visit(Assignment_operator Assignment_operator)
	{
		mAssignOperators.addLast(AssignAddSub.Assign);
	}
	
	public void visit(Mul_and_assign_right Mul_and_assign_right)
	{
		mAssignOperators.addLast(AssignAddSub.MulAssign);
	}
	
	public void visit(Div_and_assign_right Div_and_assign_right)
	{
		mAssignOperators.addLast(AssignAddSub.DivAssign);
	}
	
	public void visit(Mod_and_assign_right Mod_and_assign_right)
	{
		mAssignOperators.addLast(AssignAddSub.ModAssign);
	}
	
	public void visit(Assignment_operation_single Assignment_operation_single)
	{
		AssignAddSub pomOp = mAssignOperators.removeLast();	
		if (Assignment_operation_single.getDesignator().obj.getKind() != Obj.Elem)
		{
			if (pomOp != AssignAddSub.Assign)
			{
				Code.load(Assignment_operation_single.getDesignator().obj);
				Code.put(Code.dup_x1);
				Code.put(Code.pop);
				putOperationOnStack(pomOp);
				Code.put(Code.dup);
				Code.store(Assignment_operation_single.getDesignator().obj);
				Code.put(Code.pop);
				/*
				if(needPop)
				{
					Code.put(Code.pop);
					Code.put(Code.pop);
					needPop = false;
				}
				*/
			}
			else
			{
				Code.store(Assignment_operation_single.getDesignator().obj);
				/*
				if(needPop)
				{
					Code.put(Code.pop);
					Code.put(Code.pop);
					needPop = false;
				}
				*/
			}
		}
		else
		{
			if (pomOp != AssignAddSub.Assign)
			{	
				
				Code.put(Code.dup_x2);
				Code.put(Code.pop);
				Code.load(Assignment_operation_single.getDesignator().obj);
				putOperationOnStack(pomOp);
				Code.store(Assignment_operation_single.getDesignator().obj);
			}
			else
			{
				Code.store(Assignment_operation_single.getDesignator().obj);
				Code.put(Code.pop);
				Code.put(Code.pop);
			}	
		}
	}
	
	public void visit(Add_Op_Assignment_Designator_List Add_Op_Assignment_Designator_List)
	{

		mAssignOperands.add(Add_Op_Assignment_Designator_List.getDesignator().obj);
	}
	
	public void visit(Assignment_addition_list Assignment_addition_list)		
	{
		mAssignOperands.add(Assignment_addition_list.getDesignator1().obj);
		mAssignOperands.add(Assignment_addition_list.getDesignator().obj);

		AssignAddSub currentOp;
		Obj currentObj;
		while(mAssignOperands.size() > 0)
		{
			currentObj = mAssignOperands.removeFirst();	
			currentOp  = mAssignOperators.removeLast();
			
			if(currentObj.getKind() != Obj.Elem)
			{
				if (currentOp != AssignAddSub.Assign)
				{
					Code.load(currentObj);
					Code.put(Code.dup_x1);
					Code.put(Code.pop);
					putOperationOnStack(currentOp);
					Code.put(Code.dup);
					Code.store(currentObj);
				}
				else 
				{
					Code.put(Code.dup);
					Code.store(currentObj);
				}
			}
			else
			{
				if (currentOp != AssignAddSub.Assign)
				{	
					Code.put(Code.dup_x2);
					Code.put(Code.pop);
					Code.load(currentObj);
					putOperationOnStack(currentOp);
					Code.put(Code.dup_x2);
					Code.store(currentObj);
				}
				else 
				{	
					Code.store(currentObj);
					Code.load(currentObj);
				}
				
			}
		}
		
		Code.put(Code.pop);
		needPop = false;
	}
	
	public void putOperationOnStack(AssignAddSub op)
	{
		switch(op) 
		{
		case AddAssign:
			Code.put(Code.add);
			break;
		case SubAssign:
			Code.put(Code.sub);
			break;
		case MulAssign:
			Code.put(Code.mul);
			break;
		case DivAssign:
			Code.put(Code.div);
			break;
		case ModAssign:
			Code.put(Code.rem);
			break;
		default:
			break;
		}	
	}
}
