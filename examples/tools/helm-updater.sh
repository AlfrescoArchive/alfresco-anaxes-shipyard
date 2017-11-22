#!/usr/bin/env bash

# ----------------------------------------------------------------------
# Check bash version

# This has to be performed before anything else, otherwise bash 3 will
# report the bash 4 syntax as errors

# Check we have bash 4
_check_bash () {
  local error_message

  [ "${BASH_VERSINFO[0]}" -lt 4 ] ||  return 0

  echo "Script requires bash 4 or later. This is ${BASH_VERSION}"
  exit 1
}

_check_bash 

# ----------------------------------------------------------------------
# Utility functions

# Just Print Usage To STDERR
# If passed a value, exit() using that value otherwise return
_usage () {
  local exit_value="$1"

  cat <<EOF >&2
usage: ${command_friendly_version} is configured via environment variables

        helm_chart_repo='http://kubernetes-charts.alfresco.com/incubator/' \\
        git_repo='https://github.com/Alfresco/charts.git' \\
        git_branch='PROJECT-1234' \\
        git_repo_subdir='incubator' \\
        chart_source_dirs='application-one','application-two' \\
        ${command_name}

  (Note that chart_source_dirs is a comma separated list, with no extraneous 
  whitespace.)

  Extra environment variables for debugging include
        helm_updater_cleanup='true'|'false' (default is 'true')
        helm_updater_debug='true'|'false'
EOF

  [ -n "${exit_value}" ] && exit "${exit_value}"
}

# Wrapper around exit for errors
_echo () {
  echo "[${command_friendly_version}] $*" >&2
}

_warn () {
  _echo "[WARN] $*"
}

_error () {
  _echo '************************************************************************'
  _echo '************************************************************************'
  _echo "[ERROR] Fatal error: $*"
  _echo '************************************************************************'
  _echo '************************************************************************'
}

_exit () {
  local exit_value="$1"
  shift
  _error "$*"
  exit "${exit_value}"
}

_debug () {
  [ "${_debug_flag}" = "true" ] && _echo "[DEBUG] $*"
}

_info () {
  _echo "[INFO] $*"
}

_announce () {
  _info "Entering function $*"
}

# Cheap (non-getopt) way of checking for -h etc.
_check_help () {
  local -r check_help="$1"

  [ -z "${check_help}" ] && return 0
  [ "${check_help}" = '-?' ] || \
    [ "${check_help}" = '-h' ] || \
    [ "${check_help}" = '--help' ] && _usage 0
}

# Create secure temp dir
_mkdtemp () {
  _announce "${FUNCNAME[0]}"

  local -r tmp_dir=$(mktemp -d)
  [ -z "${tmp_dir}" ] && return 1
  [ ! -d "${tmp_dir}" ] && return 1

  echo "${tmp_dir}"
  return 0
}

# Remove temp dirs
_remove_tmp_dirs () {
  # Don't announce this
  _announce "${FUNCNAME[0]}" "$*"

  _remove_tmp_dirs_single () {
    local -r tmp_dir="$1"

    # 5 is a bit arbitrary, but if you were root, it stops deleting
    # for example, /tmp/ 
    local -r min_path_len=5

    _debug "Contents of ${tmp_dir}"
    [ "${_debug_flag}" = "true" ] && ls -l "${tmp_dir}"

    [ ${#tmp_dir} -gt ${min_path_len} ] && \
      [ -d "${tmp_dir}" ] && \
      rm -rf "${tmp_dir}"
  }

  local dir
  for dir in "$@"
  do
    _remove_tmp_dirs_single "${dir}"
  done
}

# Global scope variables for Utility functions
declare _debug_flag

# ----------------------------------------------------------------------
# Application specific functions

# Clone the charts repo
_clone_charts () {
  _announce "${FUNCNAME[0]}" "$*"

  # Temp directory to clone chart git repo into
  local -r tmp_git_dir="$1"

  # Clone the charts repo
  local error_message
  error_message=$(git clone "${git_repo}" "${tmp_git_dir}" 2>&1 ) && return 0

  echo "${error_message}"
  return 1
}

_create_branch () {
  _announce "${FUNCNAME[0]}" "$*"

  # The git branch this will work on
  local -r git_branch="$1"
  
  # Temp directory the chart git repo was cloned into
  local -r tmp_git_dir="$2"

  # Create the branch
  pushd "${tmp_git_dir}" &>> /dev/null
  git checkout -B "${git_branch}" || return 1
  popd &>> /dev/null

  return 0
}

# Package all the helm charts to a separate folder
_package_charts () {
  _announce "${FUNCNAME[0]}" "$*"

  # Temp directory to package chart into and then merge chart index.yaml
  local -r tmp_git_dir="$1"
  local -r tmp_chart_dir="$2"

  local -r chart_dir_in_tmp_git_dir="${tmp_git_dir}${git_repo_subdir:+/${git_repo_subdir}}"
  
  # Convert comma separated string into an array
  local -a chart_source_dirs_array
  readarray -td, chart_source_dirs_array <<<"${chart_source_dirs}"

  # Grab the commit message for chart_source_dir(s) - we assume that
  # all the charts being packaged are coming from a single git repo
  # This is a global.
  git_commit_message="$(git -C "${chart_source_dirs_array[0]}" log --pretty=format:%s --max-count=1)" || return 1

  # Package each chart into the tmp_chart_dir
  # We also copy them into the chart_dir in the tmp_git_dir
  local chart_source_dir
  for chart_source_dir in "${chart_source_dirs_array[@]}"
  do
    chart_source_dir=$(echo "${chart_source_dir}" | tr -d '\r\n')
    _info "Packaging ${chart_source_dir} into ${tmp_chart_dir}"
    helm package --dependency-update --destination "${tmp_chart_dir}" "${chart_source_dir}" || return 1
  done

  # Copy to git. This is ugly, and I'd prefer to have copied the
  # charts individually in the for loop above
  cp -f "${tmp_chart_dir}"/*.tgz "${chart_dir_in_tmp_git_dir}/"

  return 0
}

# Create an updated index.yaml
# The helm merge command has to run in the same folder as 
# index.yaml, and the package, which is a bit rubbish
_update_index () {
  _announce "${FUNCNAME[0]}" "$*"

  local -r tmp_git_dir="$1"
  local -r tmp_chart_dir="$2"

  # Helm Chart Index.
  # Insert an extra / if $git_repo_subdir is set
  local -r chart_dir_in_tmp_git_dir="${tmp_git_dir}${git_repo_subdir:+/${git_repo_subdir}}"
  local -r source_yaml="${chart_dir_in_tmp_git_dir}/index.yaml"

  _debug "Contents of tmp chart dir ${tmp_chart_dir}"
  [ "${helm_updater_debug}" = "true" ] && ls -l "${tmp_chart_dir}"
  _debug "Contents of chart dir in tmp git dir ${chart_dir_in_tmp_git_dir}"
  [ "${helm_updater_debug}" = "true" ] && ls -l "${chart_dir_in_tmp_git_dir}"

  # Get the correct index.yaml
  cp -f "${source_yaml}" "${tmp_chart_dir}"

  # We have to work where the chart is
  pushd "${tmp_chart_dir}" &>> /dev/null

  # This merges this package into the (copy of) index.yaml

  _debug "Contents of index.yaml - pre-merge"
  [ "${helm_updater_debug}" = "true" ] && cat index.yaml
  _debug "Contents of tmp chart dir ${PWD}"
  [ "${helm_updater_debug}" = "true" ] && ls -l .

  helm repo index \
    --merge index.yaml \
    --url "${helm_chart_repo}" \
    . || return 1

  _debug "Contents of index.yaml - post-merge "
  [ "${helm_updater_debug}" = "true" ] && cat index.yaml

  # Go back to whence we came
  popd &>> /dev/null

  # Copy back to the source git repo
  cp -f "${tmp_chart_dir}/index.yaml" "${source_yaml}"

  cat "${source_yaml}"
  return 0
}

# Automatically push back to git
_publish_charts () {
  _announce "${FUNCNAME[0]}" "$*"

  local -r tmp_git_dir="$1"

  # To the git directory!
  pushd "${tmp_git_dir}" &>> /dev/null

  # Add in new files, commit

  # In case we're using git 1.x: we don't want to push all branches

  git config --global push.default simple

  git add . || return 1
  git commit -m "${git_commit_message}" || return 1
  git push -f -u origin "${git_branch}" || return 1

  # Go back to whence we came
  popd &>> /dev/null
  return 0
}

# Check all variables are set
_check_variables () {
  _announce "${FUNCNAME[0]}"

  ${helm_updater_debug:='false'}
  ${helm_updater_cleanup:='true'}

  # FIXME - I can't get this working in this function
  # _debug_flag=${helm_updater_debug:-'false'}

  # Announce what debugging settings are enabled
  _debug "Debugging is on" 
  _debug "helm_updater_cleanup is set to ${helm_updater_cleanup}"

  local error_message

  [ -z "${helm_chart_repo}" ] && error_message="helm_chart_repo is unset"
  [ -z "${git_repo}" ] && error_message="git_repo is unset"
  [ -z "${git_branch}" ] && error_message="git_branch is unset"
  [ -z "${chart_source_dirs}" ] && error_message="no chart_source_dirs set"

  [ -z "${error_message}" ] && return 0
  echo "${error_message}"
  return 1
}

# Wrapper around _remove_tmp_dirs() and more (if needed)
_clean_up () {
    _announce "${FUNCNAME[0]}" "$*"

  [ "${helm_updater_cleanup}" = "true" ] && _remove_tmp_dirs "$@"
}

# Entry point of program after setting all the globals from command-line args
main () {
  # Tidy up on exit
  _err_exit () {
    local -r exit_value="$1"
    local -r error_message="$2"
    local -r usage="$3"

    _clean_up "${tmp_git_dir}" "${tmp_chart_dir}"
    [ -n "${usage}" ] && _usage
    _exit "${exit_value}" "${error_message}"
  }

  # Used for error messages
  local message

  # Check for help
  _check_help "$1"

  # Sanity check
  message=$(_check_variables) || _err_exit 1 "${message}" "usage"

  # Create a temp directory to clone chart git repo into
  local tmp_git_dir
  tmp_git_dir=$(_mkdtemp) || _err_exit 1 "Creating git dir failed"
  _debug "tmp_git_dir  = ${tmp_git_dir}"

  # Create a temp directory to package chart into and then merge chart 
  # index.yaml
  local tmp_chart_dir
  tmp_chart_dir=$(_mkdtemp) || _err_exit 1 "Creating chart dir failed"
  _debug "tmp_chart_dir = ${tmp_chart_dir}"

  # Clone Source Git Repo for Helm Chart Repo
  message=$(_clone_charts "${tmp_git_dir}") || \
    _err_exit 1 "Cloning charts failed ${message}"

  # Create Branch
  message=$(_create_branch "${git_branch}" "${tmp_git_dir}") || \
    _err_exit 1 "Creating branch failed ${message}"

  _package_charts "${tmp_git_dir}" "${tmp_chart_dir}" || \
    _err_exit 1 "Packaging charts failed"
  
  # Update index.yaml
  _update_index "${tmp_git_dir}" "${tmp_chart_dir}" || \
    _err_exit 1 "Updating index file failed"

  # Push changes back to Source Git Repo for Helm Chart Repo
  _publish_charts "${tmp_git_dir}" || \
     _err_exit 1 "Publishing charts failed"

  _info "SUCCESS"

  # Tidy up at the end
  _clean_up "${tmp_git_dir}" "${tmp_chart_dir}"

  exit 0
}

########################################################################
# Global Scope
#
# Parse command line arguments, but environment variables have priority
########################################################################

# Script Version
declare -r version="1.0.0"

# Calculated Variables
declare command_name
command_name="$(basename "$0" || echo "$0")"
declare -r command_friendly_version="${command_name} ${version}"

# Declare all environment variables for shellcheck [SC2154]
declare helm_chart_repo
declare git_repo
declare git_branch
declare git_repo_subdir
declare git_commit_message
declare chart_source_dirs
# Debugging options
declare helm_updater_debug
declare helm_updater_cleanup

# Global scope variables for Utility functions
# FIXME - I can't get this working in a function
_debug_flag=${helm_updater_debug:-'false'}

# Call main() if we're not sourced
[[ "${BASH_SOURCE[0]}" == "${0}" ]] && main "$@"
