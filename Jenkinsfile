node {
  stage('mkdir') {
    sh 'mkdir out'
  }

  try {
    stage('Pull') {
      git 'joshua@ovh1.josh.cafe:/home/joshua/repos/rsmm.git'    
    }

    stage('Build Gem') {
      dir('ruby/rsmm') {
        sh 'gem build rsmm.gemspec'
      }
      sh 'mv ruby/rsmm/*.gem ./out'
    }

    stage('makepkg') {
      dir('deploy/arch') {
        sh 'makepkg -c'
      }
      sh 'mv deploy/arch/*.pkg.tar.xz ./out'
    }

    stage('Archive') {
      archive './*'
    }

    dir('./out') {
      stage('Upload to gems.josh.cafe') {
        sh 'gem inabox ./*.gem'
      }

      stage('Upload to repo.josh.cafe') {
        sh 'cp ./*.pkg.tar.xz /srv/http/pacman'
        sh 'repo-add /srv/http/pacman/repo.josh.cafe.db.tar.xz /srv/http/pacman/rsmm-desktop*.pkg.tar.xz'
      }
    }
  } finally {
    stage('Cleanup') {
      dir('./out') {
        deleteDir()
      }
    }
  }

}
