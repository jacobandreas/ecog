#!/usr/bin/ruby

javac = "/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Commands/javac"

timestamp=`date +%Y-%m-%d_%H-%M-%S`.strip
work_dir="/work2/jda/ecog/work/#{timestamp}"

ssh_command_1 = <<-eof
  mkdir "#{work_dir}";
eof

ssh_command_2 = <<-eof
  cd "#{work_dir}";
  qsub ../../pbs.sh;
eof

`find . | grep "\.java$" | xargs #{javac}`
`jar cf ecog.jar -C src`
`ssh zen.millennium.berkeley.edu "#{ssh_command_1}"`
`scp ecog.jar zen.millennium.berkeley.edu:#{work_dir}/ecog.jar`
`ssh zen.millennium.berkeley.edu "#{ssh_command_2}"`
