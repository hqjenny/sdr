#=======================================================================
# UCB Chisel Flow: Makefile 
#-----------------------------------------------------------------------
# Brian Zimmer (bmzimmer@eecs.berkeley.edu)
#
# This makefile will generate verilog files or an emulator from chisel code


project_main := sha3.Sha3AccelMain
src_files    := Makefile src/main/scala/sha3/*.scala 

tester_main  := sha3.sha3Tester
test_files   := Makefile src/test/scala/sha3/*.scala 

verilog_timestamp   := build/timestamp    
vcs_timestamp       := test_run_dir/vcs/timestamp
verilator_timestamp := test_run_dir/verilator/timestamp
firrtl_timestamp    := test_run_dir/firrtl/timestamp

firrtl_timestamp_all := test_run_dir/timestamp_all

#-----------------------------------------------------------------------
# Run Unit Tests
#-----------------------------------------------------------------------
# 	By not specifying a main, sbt will prompt for a main.
test-unit: test-unit-firrtl

test-unit-firrtl: $(src_files) $(test_files)
	sbt "test:run --backend-name firrtl    --target-dir test_run_dir/firrtl"

test-unit-verilator: $(src_files) $(test_files)
	sbt "test:run --backend-name verilator --target-dir test_run_dir/verilator"

test-unit-vcs: $(src_files) $(test_files)
	sbt "test:run --backend-name vcs       --target-dir test_run_dir/vcs"
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
# Run All Tests
#-----------------------------------------------------------------------
test-all: test-all-firrtl

test-all-firrtl: $(firrtl_timestamp_all)

$(firrtl_timestamp_all): $(src_files) $(test_files)
	sbt test
	date > $(firrtl_timestamp_all)
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
# Run Top Level Test
#-----------------------------------------------------------------------
test: test-firrtl

test-verilator: $(verilator_timestamp)

test-vcs: $(vcs_timestamp)

test-firrtl: $(firrtl_timestamp)

$(verilator_timestamp): $(src_files) $(test_files)
	sbt "test:run-main $(tester_main) --backend-name verilator --target-dir test_run_dir/verilator"
	date > $(verilator_timestamp)

$(vcs_timestamp): $(src_files) $(test_files)
	sbt "test:run-main $(tester_main) --backend-name vcs       --target-dir test_run_dir/vcs"
	date > $(vcs_timestamp)

$(firrtl_timestamp): $(src_files) $(test_files)
	sbt "test:run-main $(tester_main) --backend-name firrtl    --target-dir test_run_dir/firrtl"
	date > $(firrtl_timestamp)
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
# Create build directory if it doesn't already exist
#-----------------------------------------------------------------------
build:
	mkdir build
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
# Generate Verilog Files
#-----------------------------------------------------------------------
$(verilog_timestamp): $(src_files)
	sbt "run-main $(project_main) --target-dir build"
	date > $(verilog_timestamp)

verilog: $(verilog_timestamp)
#-----------------------------------------------------------------------

#-----------------------------------------------------------------------
#  Reports
#-----------------------------------------------------------------------
reports: firrtl-report vcs-report

firrtl-report: build/firrtl-report

vcs-report: build/vcs-report

build/firrtl-report: $(src_files) $(test_files) build
	sbt "test:run-main $(tester_main) --backend-name firrtl    --target-dir test_run_dir/firrtl" > build/firrtl-report

build/vcs-report: $(src_files) $(test_files) build
	sbt "test:run-main $(tester_main) --backend-name vcs       --target-dir test_run_dir/vcs" > build/vcs-report
#-----------------------------------------------------------------------

clean:
	rm -rf build test_run_dir target csrc ucli.key

.PHONY: test test-unit test-unit-firrtl test-unit-verilator test-unit-vcs test-all test-all-firrtl verilator vcs firrtl verilog reports firrtl-report vcs-report clean
