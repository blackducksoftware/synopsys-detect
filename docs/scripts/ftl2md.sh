#!/bin/bash

templatesDir="docs/templates/content"
markdownDir="docs/markdown"
if [ -d "$templatesDir" ]
then
  echo "$templatesDir is a directory."
else
  echo "$templatesDir is not a directory. You need to run from the root of the Detect project (e.g. src/synopsys-detect)"
  exit 1
fi
if [ -d "$markdownDir" ]
then
  echo "$markdownDir already exists."
else
  mkdir $markdownDir
fi

convertOneScript=/tmp/convertOneDocFile.sh
echo "#!/bin/bash" > ${convertOneScript}
echo "# Arg: path of .ftl file relative to docs/templates/content" >> ${convertOneScript}
echo "f=\$1" >> ${convertOneScript}
echo "basename=\"\${f%.*}\"" >> ${convertOneScript}
echo "targetfile=../../markdown/\${basename}.md" >> ${convertOneScript}
echo "targetdir=\$(dirname \$targetfile)" >> ${convertOneScript}
echo "mkdir -p \${targetdir}" >> ${convertOneScript}
echo "echo \">>> \$f -> \${targetfile}\"" >> ${convertOneScript}
echo "sed 's/\\\${\\([a-z_]*\\)}/[\\1]/g'  \${basename}.ftl > ../../markdown/\${basename}.md" >> ${convertOneScript}
echo "echo \"CONCERNS:\"" >> ${convertOneScript}
echo "grep '\\[.*\\[' ../../markdown/\${basename}.md" >> ${convertOneScript}

chmod +x ${convertOneScript}

#echo "SCRIPT:"
#cat ${convertOneScript}
#echo "==="

cd $templatesDir
pwd
find . -name "*.ftl" -exec $convertOneScript {} \;

