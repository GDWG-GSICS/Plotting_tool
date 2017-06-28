<div align="center">
  <a href=http://gsics.wmo.int/>
    <img src="https://raw.githubusercontent.com/GDWG-GSICS/Plotting_tool/master/src/org/eumetsat/usd/gcp/client/resources/images/GSICS_logo_OPE.jpg" alt="GSICS Homepage" />
  </a>
</div>

<h1 align="center">GSICS Plotting Tool</h1>

<div align="center">
 A web application that allows visualising the GSICS products stored in any GSICS server.
</div>

<br />

## Introduction
This repository follows a **centralised version control workflow**. That is, each user contributing to the project clones the central remote repository to its local machine, performs the changes in its local working directory (performing local commits when needed), and finally pushes the commits back to the central remote repository (origin).

For this approach, all the users contributing to the central remote repository are added as collaborators with the same access rights as each other.

## How to Contribute
1. Make sure git is installed in your computer.
2. Clone this repository to your local one:
```
git clone https://github.com/GDWG-GSICS/Plotting_tool.git
```
3. Perform your changes, committing to your local repository after each change:
   1. Add file for next commit (stage).
   ```
   git add path/to/file.ext
   ```
   2. Remove file from next commit (unstage).
   ```
   git reset path/to/file.ext
   ```
   3. Remove file and add removal to next commit (remove and stage removal).
   ```
   git rm path/to/file.ext
   ```
   4. Check what is going to be included on next commit.
   ```
   git status
   git diff --staged
   ```
   5. Check specific changes on a file.
   ```
   git diff path/to/file.ext
   ```
   6. Revert changes.
   ```
   git reset path/to/file.ext # if file has been already staged
   git checkout -- path/to/file.ext
   ```
   7. Commit all staged changes.
   ```
   git commit -m "<Commit comment>"
   ```
4. Build the project with ant.
```
ant build
```
5. If build has been successful, test the application by deploying the resulting <code>plotter.war</code> into your tomcat server.

6. If test is successful, you can push your changes to the central remote repository:
   1. Check changes which are going to be pushed, and any possible conflicts with other contributors' changes.
   ```
   git fetch origin
   git diff origin/master
   ```
   2. Solve the conflicts if any and commit modified files.
   3. If there were other contributors' changes fetched, rebuild the project and retest (steps 4. and 5.).
   4. Push your changes to the central remote repository.
   ```
   git pull origin master
   git push
   ```

## How to Undo a contribution
In order to undo a specific commit already pushed to the central remote repository, follow these steps:
1. Get the commit id by running <code>git log</code>.
2. Revert the specific commit.
```
git revert <commit_id>
git commit -m "revert commit <commit_id>"
git push
```

This is the recommended way to revert commits as the history is not changed and reverts are added to it.
