#!/bin/bash
#
set -e
source config.env

# extract

cp "${AIR_GAP_ZIP_LOCATION}" .

#build

sed -e "s/RELEASE_VERSION/$RELEASE_VERSION/g" -e "s/UBI_VERSION/$UBI_VERSION/g" Dockerfile.template > Dockerfile

FULL_IMAGE_NAME="${TARGET_REPO}/${TARGET_IMAGE}:${TARGET_IMAGE_TAG}"
REGISTRY=registry.access.redhat.com
IMAGE=ubi8/ubi
TAG="${UBI_VERSION}"

docker build --pull -t "${FULL_IMAGE_NAME}"  \
	--build-arg BASE_REGISTRY="${REGISTRY}" \
  --build-arg BASE_IMAGE="${IMAGE}" \
	--build-arg BASE_TAG="${TAG}" .
