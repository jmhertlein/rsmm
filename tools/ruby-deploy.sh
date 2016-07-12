DEPLOY_UUID=$(cat /proc/sys/kernel/random/uuid)
TMP_DIR=/tmp/deploy-"$DEPLOY_UUID"
mkdir -p "$TMP_DIR"

cd ../ruby

cd trade-config
gem build trade-config.gemspec
mv *.gem "$TMP_DIR"
cd ..

cd ge-api
gem build ge-api.gemspec
mv *.gem "$TMP_DIR"
cd ..

cd trade-db
gem build trade-db.gemspec
mv *.gem "$TMP_DIR"
cd ..

cd gescrape
gem build gescrape.gemspec
mv *.gem "$TMP_DIR"
cd ..

cd "$TMP_DIR"
echo "OK to install these?"
ls
read USER_ACCEPT

if [[ "$USER_ACCEPT" == "y" ]] ; then
  gem install *.gem
fi

