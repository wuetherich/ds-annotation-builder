package com.wuetherich.osgi.ds.annotations.test.util;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class JdtProjectTestHelper {

	private static final String SIMPLE_ARTIFACT_MODEL_TEST_JDT = "SimpleArtifactModelTest-JDT";

	/**
	 * <p>
	 * </p>
	 * 
	 * @throws CoreException
	 */
	public static void deleteTestJavaProject(IJavaProject project)
			throws CoreException {

		//
		project.getProject().close(null);
		EclipseProjectUtils.deleteProjectIfExists(project.getProject()
				.getName());
	}

	// public void addSource() {
	// try {
	// IPackageFragment pack =
	// getJavaProject().getPackageFragmentRoot(getJavaProject().getProject().getFolder("src"))
	// .createPackageFragment("newPack", false, null);
	//
	// StringBuffer buf = new StringBuffer();
	// buf.append("package " + pack.getElementName() + ";\n");
	// buf.append("\n");
	// buf.append("public class NewClass {}");
	//
	// ICompilationUnit cu = pack.createCompilationUnit("NewClass.java",
	// buf.toString(), false, null);
	//
	// } catch (JavaModelException e) {
	// Assert.fail(e.getMessage());
	// }
	// }
	//
	// public void modifyClassKlasse() throws Exception {
	//
	// //
	// ICompilationUnit compilationUnit = getJavaProject()
	// .getPackageFragmentRoot(getJavaProject().getProject().getFolder("src")).getPackageFragment("de.test")
	// .getCompilationUnit("Klasse.java");
	//
	// // create an AST
	// ASTParser parser = ASTParser.newParser(AST.JLS4);
	// parser.setSource(compilationUnit);
	// parser.setResolveBindings(false);
	// CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
	// AST ast = astRoot.getAST();
	//
	// // create the descriptive ast rewriter
	// ASTRewrite rewrite = ASTRewrite.create(ast);
	//
	// // get the block node that contains the statements in the method body
	// TypeDeclaration typeDecl = (TypeDeclaration) astRoot.types().get(0);
	// MethodDeclaration methodDecl = typeDecl.getMethods()[0];
	// Block block = methodDecl.getBody();
	//
	// // String value = javax.naming.Context.AUTHORITATIVE;
	//
	// VariableDeclarationFragment fragment =
	// ast.newVariableDeclarationFragment();
	// fragment.setName(ast.newSimpleName("value"));
	//
	// Expression expression = ast.newQualifiedName(
	// ast.newQualifiedName(ast.newQualifiedName(ast.newSimpleName("value"),
	// ast.newSimpleName("value")),
	// ast.newSimpleName("Context")), ast.newSimpleName("AUTHORITATIVE"));
	// fragment.setInitializer(expression);
	// VariableDeclarationStatement statement =
	// ast.newVariableDeclarationStatement(fragment);
	// statement.setType(ast.newSimpleType(ast.newSimpleName("String")));
	//
	// // describe that the first node is inserted as first statement in block,
	// // the other one as last statement
	// // note: AST is not modified by this
	// ListRewrite listRewrite = rewrite.getListRewrite(block,
	// Block.STATEMENTS_PROPERTY);
	// listRewrite.insertFirst(statement, null);
	//
	// // evaluate the text edits corresponding to the described changes. AST
	// // and CU still unmodified.
	// TextEdit res = rewrite.rewriteAST();
	//
	// // apply the text edits to the compilation unit
	// Document document = new Document(compilationUnit.getSource());
	// res.apply(document);
	// compilationUnit.getBuffer().setContents(document.get());
	// compilationUnit.save(null, true);
	// }
	//
	// protected void removeClassKlasse() {
	//
	// try {
	// //
	// IFile file =
	// getJavaProject().getProject().getFile("src/de/test/Klasse.java");
	//
	// file.delete(true, null);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// Assert.fail(e.getMessage());
	// }
	//
	// // ICompilationUnit compilationUnit = getJavaProject()
	// // .getPackageFragmentRoot(
	// // getJavaProject().getProject().getFolder("src"))
	// // .getPackageFragment("de.test")
	// // .getCompilationUnit("Klasse.java");)
	//
	// }

	public static void createTestJavaProject() throws CoreException,
			JavaModelException {

		//
		IJavaProject javaProject = createNewJavaProject(SIMPLE_ARTIFACT_MODEL_TEST_JDT);
		IProject project = javaProject.getProject();

		//
		IFolder srcFolder = project.getFolder("src");

		// // copy the test files
		// Copy copy = new Copy();
		// copy.setProject(new Project());
		// copy.setTodir(srcFolder.getRawLocation().toFile());
		// FileSet fileSet = new FileSet();
		// fileSet.setDir(new File(TestProjectCreator
		// .getSourcesPath(TestProjectCreator
		// .getTestDataDirectory("SimpleArtifactModelTest"))));
		// copy.addFileset(fileSet);
		// copy.execute();

		// refresh and build
		srcFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
		project.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
	}

	/**
	 * @param projectName
	 * @throws CoreException
	 */
	protected static IJavaProject createNewJavaProject(String projectName)
			throws CoreException {

		// create the eclipse project
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.create(null);
		project.open(null);

		// add java nature
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);

		// create the java project
		IJavaProject javaProject = JavaCore.create(project);

		// set the bin folder
		IFolder binFolder = project.getFolder("bin");
		binFolder.create(false, true, null);
		javaProject.setOutputLocation(binFolder.getFullPath(), null);

		// set the class path (src and JDK)
		List<IClasspathEntry> entries = new LinkedList<IClasspathEntry>();
		IFolder srcFolder = project.getFolder("src");
		srcFolder.create(false, true, null);
		entries.add(JavaCore.newSourceEntry(srcFolder.getFullPath()));
		IExecutionEnvironmentsManager executionEnvironmentsManager = JavaRuntime
				.getExecutionEnvironmentsManager();
		IExecutionEnvironment[] executionEnvironments = executionEnvironmentsManager
				.getExecutionEnvironments();
		for (IExecutionEnvironment iExecutionEnvironment : executionEnvironments) {
			// We will look for JavaSE-1.6 as the JRE container to add to our
			// classpath
			if ("JavaSE-1.6".equals(iExecutionEnvironment.getId())) {
				entries.add(JavaCore.newContainerEntry(JavaRuntime
						.newJREContainerPath(iExecutionEnvironment)));
				break;
			}
		}

		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[0]),
				null);

		//
		return javaProject;
	}
}
