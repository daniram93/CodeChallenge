#!/bin/bash

source ${PWD}/config.properties

command_order="java -Xmx2g -Xms256m -XX:OnOutOfMemoryError=\"kill -9 %p\" -Dfile.encoding=UTF8 -Duser.timezone=Europe/Madrid -jar ${ORDER_JAR}"
command_catalog="java -Xmx2g -Xms256m -XX:OnOutOfMemoryError=\"kill -9 %p\" -Dfile.encoding=UTF8 -Duser.timezone=Europe/Madrid -jar ${CATALOG_JAR}"

#Log path
folder_name=$(basename ${PWD})
group_name="Execution"
alloc_index="1"


log_path="${PWD}/${group_name}_${alloc_index}.log"

#Create logs folder if not exist
log_folder=$(dirname $log_path)
if [ ! -d $log_folder ]; then
    mkdir -p $log_folder
fi


echo "[$(date)] $command_catalog" &
eval $command_catalog  >> $log_path 2>&1 &
echo "[$(date)] GETTING CATALOG"  &
    
sleep 10
echo "[$(date)] PROCESSING ORDER" &
echo "[$(date)] $command_order" &
eval $command_order  >> $log_path 2>&1
   
echo '['$(date)'] Done'

exit 1
    
