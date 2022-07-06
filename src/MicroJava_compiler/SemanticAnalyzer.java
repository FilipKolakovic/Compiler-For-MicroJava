package rs.ac.bg.etf.pp1;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemanticAnalyzer extends VisitorAdaptor {		
	
	private static Struct boolType = Tab.insert(Obj.Type, "bool", new Struct(5)).getType();
	
	private boolean errorDetected = false;
	private Obj globl = null;
	private Obj currentMethod = null;	
	private boolean returnValFound = false;
	
	private ArrayList<Var> myVars = new ArrayList<Var>();
	private ArrayList<Method> myMethods = new ArrayList<Method>();
	private ArrayList<Struct> tempArgs = new ArrayList<Struct>();
	private Logger log = Logger.getLogger(getClass());	
	
	private int getMyMethod(String desiredName) {
		for (int i = 0; i < myMethods.size(); i++) {
			if (myMethods.get(i).getMethodName().equals(desiredName)) {
				return i;
			}
		}
		return -1;
	}	

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0) {
			msg.append(" on line ").append(line).append("!");
		}
		log.error(msg.toString());
	}
	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0) {
			msg.append(" on line ").append(line);
		}
		log.info(msg.toString());
	}	

	public static Struct getBoolType() {
		return boolType;
	}
	
	public boolean isErrorDetected() {
		return errorDetected;
	}				
	
	public Obj getGlobl() {
		return globl;
	}
	
	public ArrayList<Method> getAllMethods(){
		return myMethods;
	}
	
	@Override	    			
	public void visit(Program_0 Program_0) { 
		Tab.chainLocalSymbols(globl);
		report_info("Program executing is done " + globl.getName() , Program_0);
		Tab.closeScope();
	}
	
	@Override	    			
	public void visit(Program_1 Program_1) { 
		Tab.chainLocalSymbols(globl);
		report_info("Program executing is done " + globl.getName() , Program_1);
		Tab.closeScope();
	}
	
	@Override	    			
	public void visit(Program_2 Program_2) { 
		Tab.chainLocalSymbols(globl);
		report_info("Program executing is done " + globl.getName() , Program_2);
		Tab.closeScope();
	}
	
	@Override	    			
	public void visit(Program_3 Program_3) { 
		Tab.chainLocalSymbols(globl);
		report_info("Program executing is done " + globl.getName() , Program_3);
		Tab.closeScope();
	}
	
	@Override
	public void visit(Program_name Program_name) { 		
		globl = Tab.insert(Obj.Prog, Program_name.getProgramName(), Tab.noType);
		report_info("Program is starting " + globl.getName() , Program_name);
		Tab.openScope();
	}
	
	@Override
	public void visit(Const_declaration Const_declaration) {		
		Struct type = Const_declaration.getType().struct;
		for (int i = 0; i < myVars.size(); i++) {
			report_info("Declared constant " + myVars.get(i).getName(), Const_declaration);
			Tab.insert(Obj.Con, myVars.get(i).getName(), type);		
		}
		myVars.clear();
	}	
	
	@Override
	public void visit(Const_part Const_part) { 
		if (Tab.find(Const_part.getConstName()) == Tab.noObj) {
			String varName = Const_part.getConstName();
			for (int i = 0; i < myVars.size(); i++) {
				if(myVars.get(i).getName().equals(varName)) {
					report_error("Semantic error - constant name " + Const_part.getConstName() + " already exists", Const_part);
					return;
				}
			}
			myVars.add(new Var(Const_part.getConstName()));
		} else {			
			report_error("Semantic error - constant name " + Const_part.getConstName() + " already exists", Const_part);
		}
	}
	
	@Override
    public void visit(Var_declaration Var_declaration) { 
    	Struct type = Var_declaration.getType().struct;
    	for (int i = 0; i < myVars.size(); i++) {
	    	if (myVars.get(i).isArray()) {
	    		report_info("Declared array " + myVars.get(i).getName(), Var_declaration); 
	    		Obj obj = Tab.insert(Obj.Var, myVars.get(i).getName(), new Struct(Struct.Array, type));
	    		obj.setFpPos(-1);
	    	} else {
	    		report_info("Declared var " + myVars.get(i).getName(), Var_declaration);
	    		Obj obj = Tab.insert(Obj.Var, myVars.get(i).getName(), type);
	    		obj.setFpPos(-1);
	    	}    	
    	}
    	myVars.clear();
    }    			    	
    
	@Override
    public void visit(Var_array Var_array) { 
		if (Tab.find(Var_array.getVarName()) == Tab.noObj) {
			String varName = Var_array.getVarName();
			for (int i = 0; i < myVars.size(); i++) {
				if(myVars.get(i).getName().equals(varName)) {
					report_error("Semantic error - var : " + Var_array.getVarName() + " already exists", Var_array);
					return;
				}
			}
			myVars.add(new Var(Var_array.getVarName(), true));			
		} else { 
			report_error("Semantic error - var : " + Var_array.getVarName() + " already exists", Var_array);
		}
    }
    
	@Override
    public void visit(Var_normal Var_normal) {
		Obj obj = Tab.find(Var_normal.getVarName());
		if (obj == Tab.noObj) {
			String varName = Var_normal.getVarName();
			for (int i = 0; i < myVars.size(); i++) {
				if(myVars.get(i).getName().equals(varName)) {
					report_error("Semantic error - var: " + Var_normal.getVarName() + " already exists", Var_normal);
					return;
				}
			}
			myVars.add(new Var(Var_normal.getVarName()));			
		} else { 			
			report_error("Semantic error - var : " + Var_normal.getVarName() + " already exists", Var_normal);
		}
	}
	
	@Override
    public void visit(Var_error Var_error) { visit(); }
	
	@Override
    public void visit(Type Type) {
    	Obj typeNode = Tab.find(Type.getTypeName());
		if (typeNode == Tab.noObj) {
			report_error("Semantic error - type is not found " + Type.getTypeName() + " in symboltable!", null);
			Type.struct = Tab.noType;
		} 
		else {
			if (Obj.Type == typeNode.getKind()) {
				report_info("Founded type " + Type.getTypeName() + " in symboltable", Type);
				Type.struct = typeNode.getType();
			} 
			else {
				report_error("Semantic error - name " + Type.getTypeName() + " does not represent type", Type);
				Type.struct = Tab.noType;
			}
		}
    }	

	@Override
	public void visit(Val_Num_const Val_Num_const) { 
		Val_Num_const.struct = Tab.intType;
	}
	
	@Override
	public void visit(Val_Char_const Val_Char_const) { 
		Val_Char_const.struct = Tab.charType;
	}    
	
	@Override
	public void visit(Val_Bool_const Val_Bool_const) { 
		Val_Bool_const.struct = boolType;
	}

	@Override
	public void visit(Meth_void_decl Meth_void_decl) { 
		if (returnValFound) {
			report_error("Semantic error - method " + currentMethod.getName() + " does not need to have return value!", null);
		}
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		report_info("Method processing is done " + currentMethod.getName(), Meth_void_decl);
		currentMethod = null;
		returnValFound = false;
	}
	
	public void visit(Meth_type_decl Meth_type_decl) { 
		if (!returnValFound) {
			report_error("Semantic error - method " + currentMethod.getName() + " has to have return value", null);
		}
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();		
		report_info("Method processing is done " + currentMethod.getName(), Meth_type_decl);
		currentMethod = null;
		returnValFound = false;
	}    		
	
	@Override
	public void visit(Method_void_name Method_void_name) { 
		currentMethod = Tab.insert(Obj.Meth, Method_void_name.getMethodName(), Tab.noType);
		myMethods.add(new Method(Method_void_name.getMethodName()));		
		Tab.openScope();
		report_info("start processing method " + Method_void_name.getMethodName(), Method_void_name);
	}

	@Override
	public void visit(Method_type_name Method_type_name) { 
		currentMethod = Tab.insert(Obj.Meth, Method_type_name.getMethodName(), Method_type_name.getType().struct);		
		myMethods.add(new Method(Method_type_name.getMethodName()));		
		Tab.openScope();
		report_info("start processing method " + Method_type_name.getMethodName(), Method_type_name);
	}
    
	@Override
	public void visit(Param_array Param_array) {
		if (Tab.find(Param_array.getParamName()) == Tab.noObj){
			Obj obj = Tab.insert(Obj.Var, Param_array.getParamName(), new Struct(Struct.Array, Param_array.getType().struct));
			if (myMethods.size() > 0) {
				myMethods.get(myMethods.size() - 1).getParameters().add(obj.getType());
			}			
			obj.setFpPos(currentMethod.getLevel());
			currentMethod.setLevel(currentMethod.getLevel() + 1);
		} else {
			report_error("Semantic error - name " + Param_array.getParamName() + " already exists", Param_array);
		}
	}
	
	@Override
    public void visit(Param_normal Param_normal) {
		if (Tab.find(Param_normal.getParamName()) == Tab.noObj) {
	    	Obj obj = Tab.insert(Obj.Var, Param_normal.getParamName(), Param_normal.getType().struct);
	    	if (myMethods.size() > 0) {
	    		myMethods.get(myMethods.size() - 1).getParameters().add(obj.getType());
	    	}
	    	obj.setFpPos(currentMethod.getLevel());
			currentMethod.setLevel(currentMethod.getLevel() + 1);
		} else {
			report_error("Semantic error - name " + Param_normal.getParamName() + " already exists", Param_normal);
		}
    }    
    
	@Override
    public void visit(Param_error Param_error) { visit(); }

	@Override
	public void visit(Stmt_Read Stmt_Read) {
		Obj obj = Stmt_Read.getDesignator().obj;
		if (obj.getKind() == Obj.Var || obj.getKind() == Obj.Elem) {
			if (!obj.getType().equals(Tab.intType) && !obj.getType().equals(Tab.charType) && !obj.getType().equals(boolType)) {
				report_error("Semantic error - designator read statement is not int, char or bool", Stmt_Read);
			}
		} else {
			report_error("Semantic error - designator read statement is not var or element of array", Stmt_Read);
		}
	}
	
	@Override
	public void visit(Stmt_print_0 Stmt_print_0) {
		Struct expr = Stmt_print_0.getExpr().struct;
		if (expr != null) {
			if (!expr.equals(Tab.intType) && !expr.equals(Tab.charType) && !expr.equals(boolType)) {
				report_error("Semantic error - expr from statement is not int, char or bool", Stmt_print_0);
			}
		}
	}
	
	@Override
	public void visit(Stmt_print_1 Stmt_print_1) {	
		Struct expr = Stmt_print_1.getExpr().struct;
			if (expr != null) {
			if (!expr.equals(Tab.intType) && !expr.equals(Tab.charType) && !expr.equals(boolType)) {
				report_error("Semantic error - expr from statement is not int, char or bool", Stmt_print_1);
			}		
		}
	} 
	
	public void visit(Stmt_return_val Stmt_return_val) { 
		 returnValFound = true;
		 if (!currentMethod.getType().compatibleWith(Stmt_return_val.getExpr().struct)) {
			 report_error("Semantic error - type of method and method return value are not the same", Stmt_return_val);
		 }
	}			

	@Override
	public void visit(Expr_neg_term_multi Expr_neg_term_multi) { 
		Expr_neg_term_multi.struct = Expr_neg_term_multi.getTerm().struct;
	}
	
	@Override
	public void visit(Expr_neg_term_single Expr_neg_term_single) {
		Expr_neg_term_single.struct = Expr_neg_term_single.getTerm().struct;
	}
	
	@Override
	public void visit(Expr_term_multi Expr_term_multi) { 
		Expr_term_multi.struct = Expr_term_multi.getTerm().struct;
	}
	
	@Override
	public void visit(Expr_term_single Expr_term_single) { 
		Expr_term_single.struct = Expr_term_single.getTerm().struct;
	}
	
	public void visit(Expr_error Expr_error) { visit(); }                                

	@Override
	public void visit(Term_multi Term_multi) { 
		Term_multi.struct = Term_multi.getFactor().struct;
	}
	
	@Override
	public void visit(Term_single Term_single) { 
		Term_single.struct = Term_single.getFactor().struct;
	}    

	@Override
	public void visit(Factor_num_const Factor_num_const) { 
		Factor_num_const.struct = Tab.intType;
	}
	
	@Override
	public void visit(Factor_char_const Factor_char_const) { 
		Factor_char_const.struct = Tab.charType;
	}
	
	@Override
	public void visit(Factor_expr Factor_expr) { 
		Factor_expr.struct = Factor_expr.getExpr().struct;
	}
	
	@Override
	public void visit(Factor_bool_const Factor_bool_const) { 
		Factor_bool_const.struct = boolType;
	}
	
	@Override
	public void visit(Factor_new Factor_new) {
		Factor_new.struct = Factor_new.getType().struct;
	} 
	
	@Override
	public void visit(Factor_new_array Factor_new_array) {
		if (Factor_new_array.getExpr().struct == Tab.intType) {
			Factor_new_array.struct = new Struct(Struct.Array, Factor_new_array.getType().struct);			
		} else {
			report_error("Semantic error - [expr] for new statement can be only of type int", Factor_new_array);
		}
	}
	
	@Override
	public void visit(Factor_meth_call_0 Factor_meth_call) {
		Factor_meth_call.struct = Factor_meth_call.getDesignator().obj.getType();
		if (Factor_meth_call.getDesignator().obj.getKind() == Obj.Meth) {
			report_info("Method call is found " + Factor_meth_call.getDesignator().obj.getName(), Factor_meth_call);
			int index = getMyMethod(Factor_meth_call.getDesignator().obj.getName());
			if (index != -1) { 
				Method meth = myMethods.get(index);
				if (!meth.getParameters().equals(tempArgs)) {
					report_error("Semantic error - args are not validate " + Factor_meth_call.getDesignator().obj.getName(), Factor_meth_call);
				}
			}
		} else {
			report_error("Semantic error - " + Factor_meth_call.getDesignator().obj.getName() + " is not method", Factor_meth_call);
		}
		tempArgs.clear();
	} 
	
	@Override
	public void visit(Factor_meth_call_1 Factor_meth_call) {
		Factor_meth_call.struct = Factor_meth_call.getDesignator().obj.getType();
		if (Factor_meth_call.getDesignator().obj.getKind() == Obj.Meth) {
			report_info("Method call is found " + Factor_meth_call.getDesignator().obj.getName(), Factor_meth_call);
			int index = getMyMethod(Factor_meth_call.getDesignator().obj.getName());
			if (index != -1) { 
				Method meth = myMethods.get(index);
				if (!meth.getParameters().equals(tempArgs)) {
					report_error("Semantic error - calling method with args " + Factor_meth_call.getDesignator().obj.getName(), Factor_meth_call);
				}
			}
		} else {
			report_error("Semantic error - " + Factor_meth_call.getDesignator().obj.getName() + " is not method", Factor_meth_call);
		}
		tempArgs.clear();
	} 
	
	@Override
	public void visit(Factor_designator Factor_designator) { 
		Factor_designator.struct = Factor_designator.getDesignator().obj.getType();
	}
          

	@Override
	public void visit(Designator_0 Designator_0) {
		Obj obj = Tab.find(Designator_0.getID());
		if (obj == Tab.noObj) { 
			report_error("Semantic error - name " + Designator_0.getID() + " is not declarated", Designator_0);
		}
		if (Designator_0.getExpr().struct != Tab.intType) {
			report_error("Semantic error - access of array element is not valid " + Designator_0.getID(), Designator_0);
		} else if (obj.getType().getKind() != Struct.Array){
			report_error("Semantic error - name " + Designator_0.getID() + " expected to be array", Designator_0);
		}
		Designator_0.obj = new Obj(Obj.Elem, obj.getName(), obj.getType().getElemType());
		report_info("Accessing array element " + Designator_0.getID(), Designator_0);
	} 

	@Override
	public void visit(Designator_1 Designator_1) { 
		Obj obj = Tab.find(Designator_1.getID());
		if (obj == Tab.noObj) { 
			report_error("Semantic error - name " + Designator_1.getID() + " is not declarated", Designator_1);
		}		
		Designator_1.obj = obj;
	}

	@Override
	public void visit(Assignment_operation_single Assignment_operation_single) {		
		Struct left = Assignment_operation_single.getDesignator().obj.getType();
		Struct right = Assignment_operation_single.getExpr().struct;		
		if (!right.assignableTo(left)) {
			report_error("Semantic error - Incompatible types", Assignment_operation_single);
		}
	}

	@Override
	public void visit(Increment Increment) { 
		if (Increment.getDesignator().obj.getType() != Tab.intType) {
			report_error("Semantic error - type for increment statement can be only int", Increment);
		}
	}
	
	@Override
	public void visit(Decrement Decrement) { 
		if (Decrement.getDesignator().obj.getType() != Tab.intType) {
			report_error("Semantic error - type for increment statement can be only int", Decrement);
		}
	}

	@Override
	public void visit(Act_Pars_Single Act_Pars_Single) { 
		tempArgs.add(Act_Pars_Single.getExpr().struct);
	}
	
	@Override
	public void visit(Act_Pars_Multi Act_Pars_Multi) { 
		tempArgs.add(Act_Pars_Multi.getExpr().struct);
	}    	

	@Override
	public void visit(Method_call_0 Method_call_0) {
		if (Method_call_0.getDesignator().obj.getKind() == Obj.Meth) {
			report_info("Method call is found " + Method_call_0.getDesignator().obj.getName(), Method_call_0);
			int index = getMyMethod(Method_call_0.getDesignator().obj.getName());
			if (index != -1) { 
				Method meth = myMethods.get(index);
				if (!meth.getParameters().equals(tempArgs)) {
					report_error("Semantic error - args are not validate " + Method_call_0.getDesignator().obj.getName(), Method_call_0);
				}
			}
		} else {
			report_error("Semantic error - " + Method_call_0.getDesignator().obj.getName() + " is not method ", Method_call_0);
		}
		tempArgs.clear();
	}

	@Override
	public void visit(Method_call_1 Method_call_1) { 
		if (Method_call_1.getDesignator().obj.getKind() == Obj.Meth) {   
			report_info("Method call is found " + Method_call_1.getDesignator().obj.getName(), Method_call_1);
			int index = getMyMethod(Method_call_1.getDesignator().obj.getName());
			if (index != -1) { 
				Method meth = myMethods.get(index);
				if (!meth.getParameters().equals(tempArgs)) {
					report_error("Semantic error - args are not validate " + Method_call_1.getDesignator().obj.getName(), Method_call_1);
				}
			}
		} else {
			report_error("Semantic error - " + Method_call_1.getDesignator().obj.getName() + " is not method", Method_call_1);
		}
		tempArgs.clear();
	}    
}
