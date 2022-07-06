package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java_cup.runtime.Symbol;
import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;

public class Compiler {

	private static Logger log = Logger.getLogger(Compiler.class);
	
	public static void main(String[] args) throws Exception {
		Logger log = Logger.getLogger(Compiler.class);
		if (args.length < 2) {
			log.error("not enough args! Expected input:\n<input_file>.mj <output_file>.obj");
			return;
		}
		
		File sourceCode = new File(args[0]);
		if (!sourceCode.exists()) {
			log.error("Input file: " + sourceCode.getAbsolutePath() + " is not found...");
			return;
		}
			
		log.info("Compiling of input MJ file: " + sourceCode.getAbsolutePath());
		
		try (BufferedReader br = new BufferedReader(new FileReader(sourceCode))) {
			MJLexer lexer = new MJLexer(br);
			
			MJParser p = new MJParser(lexer);
			//parsiranje
	        Symbol s = p.parse();  
	        SyntaxNode prog = (SyntaxNode)(s.value);
	        
	        if (p.isErrorDetected()) {
				log.info("Input file has syntax errros!");
			} else {
				log.info("\n\n\nSyntax tree\n\n\n");
				log.info("\n" + prog.toString());
				log.info("*********************************");
				
				//pocetni opseg
				Tab.init();
					
				SemanticAnalyzer semanticCheck = new SemanticAnalyzer();
				prog.traverseBottomUp(semanticCheck);
		        Tab.dump();
				log.info("\n\n\nSymbol Table\n\n\n");
				tsDump();
				log.info("*********************************");
				
		        if (!semanticCheck.isErrorDetected()) {
		        	File objFile = new File(args[1]);
		        	log.info("Generating bitecode of MJ: " + objFile.getAbsolutePath());
		        	if (objFile.exists())
		        		objFile.delete();
		        	
		        	//Generisanje koda
		        	CodeGenerator codeGenerator = new CodeGenerator(semanticCheck.getGlobl());
		        	prog.traverseBottomUp(codeGenerator);
		        	Code.write(new FileOutputStream(objFile));
		        	log.info("Parsing is successfully done!");
		        }
		        else {
		        	log.error("Parsing is failed!");
		        }
			}
		}
	}
	
	private static boolean addTab = false;
	
	private static String collOfObjDump(Collection<Obj> coll) {
		StringBuilder sb = new StringBuilder();
		Iterator<Obj> i = coll.iterator();
		Obj obj;
		while (i.hasNext()) {
			obj = (Obj) i.next();
			sb.append(objDump(obj));
		}
		return sb.toString();
	}
	
	private static String objDump(Obj obj) {
		StringBuilder sb = new StringBuilder();		
		switch (obj.getKind()) {			
			case Obj.Type:		
				sb.append("Type " + obj.getName() + ": ");
				sb.append(structDump(obj.getType()));
				break;
			case Obj.Prog:
				addTab = true;
				sb.append("\n");
				sb.append("Prog " + obj.getName() + ": ");				
				sb.append(structDump(obj.getType()));						
				Collection<Obj> globlSyms = obj.getLocalSymbols();				
				sb.append(collOfObjDump(globlSyms));
				break;
			case Obj.Meth:
				addTab = true;
				sb.append("Meth " + obj.getName() + ": ");				
				sb.append(structDump(obj.getType()));
				//sb.append("(br param:" + obj.getLevel() + ", br lok: " + (obj.getLocalSymbols().size() - obj.getLevel()) + ")");
				Collection<Obj> localSyms = obj.getLocalSymbols();				
				sb.append(collOfObjDump(localSyms));
				break;
			case Obj.Var:
				if (addTab) {
					sb.append("\t");
				}				
				if (obj.getLevel() == 0) {
					sb.append("Globl ");
				} else if (obj.getLevel() == 1) {
					if (obj.getFpPos() == -1) {
						sb.append("Local ");
					} else {
						sb.append("Param" + obj.getFpPos() + " ");
					}					  
				}
				sb.append("Var " + obj.getName() + ": ");				
				sb.append(structDump(obj.getType()));				
				break;
			case Obj.Con:
				if (addTab) {
					sb.append("\t");
				}
				sb.append("Con " + obj.getName() + ": ");				
				sb.append(structDump(obj.getType()));				
				break;			
		}		
		return sb.toString();
	}
	
	private static String structDump(Struct struct) {
		StringBuilder sb = new StringBuilder();
		if (struct.getKind() == Struct.Array) {
			sb.append("array of ");
			sb.append(structDump(struct.getElemType()));
		} else {
			switch (struct.getKind()) {
				case Struct.Int:
					sb.append("int");
					break;
				case Struct.Char:
					sb.append("char");
					break;
				case Struct.None:
					sb.append("none");
					break;
				default:
					sb.append("bool");
					break;
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static String tsdump() {
		StringBuilder sb = new StringBuilder();
		for (Scope s = Tab.currentScope; s != null; s = s.getOuter()) {
			Collection<Obj> coll = s.values();
			sb.append(collOfObjDump(coll));
		}
		return sb.toString();
	}

	public static void tsDump() {
		log.info("\n\n" + tsdump());
	}
}
